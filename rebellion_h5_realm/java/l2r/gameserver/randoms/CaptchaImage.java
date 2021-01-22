/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.randoms;

import l2r.commons.util.Rnd;
import l2r.gameserver.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.htm.HtmCache;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.listener.actor.OnDeathListener;
import l2r.gameserver.model.Creature;
import l2r.gameserver.model.Player;
import l2r.gameserver.model.actor.listener.CharListenerList;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.PledgeCrest;
import l2r.gameserver.network.serverpackets.Say2;
import l2r.gameserver.network.serverpackets.components.ChatType;
import l2r.gameserver.network.serverpackets.components.CustomMessage;
import l2r.gameserver.skills.AbnormalEffect;
import l2r.gameserver.tables.AdminTable;
import l2r.gameserver.utils.AutoBan;
import l2r.gameserver.utils.Location;
import l2r.gameserver.utils.Log;

import gnu.trove.map.hash.TIntLongHashMap;
import gov.nasa.worldwind.formats.dds.DDSConverter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Infern0, Nik
 * 
 */
public class CaptchaImage implements OnDeathListener
{
	private static final Logger _log = LoggerFactory.getLogger(CaptchaImage.class);
	
	private static final byte CAPTCHA_REQUESTS = 28;
	private static final byte CAPTCHA_TRIES = 33;
	private static final byte CAPTCHA_CODE = 38; // 5 bits per char, max 5 chars = 25 bits.
	private static final byte CAPTCHA_WRONG = 63;
	
	private static final char[] ELIGIBLE_CHARS =
	{
		'A',
		'B',
		'C',
		'D',
		'E',
		'F',
		'G',
		'H',
		'K',
		'L',
		'M',
		'P',
		'R',
		'S',
		'T',
		'U',
		'W',
		'X',
		'Y',
		'Z'
	};
	
	private static ScheduledFuture<?> _startTaskMessage;
	private static ScheduledFuture<?> _startTaskPunishment;
	private static TIntLongHashMap _captchaPlayers = new TIntLongHashMap(); // 1 bit for captchaWrong, 25 bits for 5 chars, 5 bits for captchaTries, 5 bits for captchaRequests, 28 bits for mobsKilled
	private static Map<Integer, Location> _lastKillLocations = new HashMap<Integer, Location>();
	
	public CaptchaImage()
	{
		CharListenerList.addGlobal(this);
	}
	
	@Override
	public void onDeath(Creature actor, Creature killer)
	{
		if(Config.ENABLE_CAPTCHA && actor.isMonster() && killer != null && killer != actor && killer.isPlayer())
		{
			Player player = killer.getPlayer();
			String customHtm = HtmCache.getInstance().getNotNull("mods/Captcha/ShowCaptchaWindow.htm", player);
			
			// +1 to mobcounter
			addKilledMob(player);
			boolean doCaptcha = CaptchaImage.getKilledMobs(player.getObjectId()) >= Rnd.get(Config.CAPTCHA_MIN_MONSTERS, Config.CAPTCHA_MAX_MONSTERS) && getCaptchaRequests(player) <= 0; // random monster number.
			
			if (Config.CAPTCHA_SAME_LOCATION_DELAY >= 0 && Config.CAPTCHA_SAME_LOCATION_MIN_KILLS >= 0 && !doCaptcha)
			{
				// Hack: use location heading as insertTime
				int locationInsertTime = (int) (System.currentTimeMillis() / 1000); // Will cause bug on 03:14:07 UTC on Tuesday, 19 January 2038 :)
				Location lastKillLoc = _lastKillLocations.get(player.getObjectId());
				if (lastKillLoc == null)
					_lastKillLocations.put(player.getObjectId(), player.getLoc().setH(locationInsertTime).setR(0));
				else if (lastKillLoc.equals(player.getLoc()))
					_lastKillLocations.put(player.getObjectId(), lastKillLoc.setR(lastKillLoc.r + 1)); // Superhack: Use location's reflection as a count for how many times its put :D:D:D
				else // Different location, put it.
					_lastKillLocations.put(player.getObjectId(), player.getLoc().setH(locationInsertTime).setR(0));
				
				// Location isnt changed for 1 minute.
				if (lastKillLoc != null && locationInsertTime - lastKillLoc.h >= Config.CAPTCHA_SAME_LOCATION_DELAY) // We work in seconds here, remember?
				{
					// More than 5 mobs killed in the last 60 secs
					if (lastKillLoc.r >= Config.CAPTCHA_SAME_LOCATION_MIN_KILLS)
						doCaptcha = true;
					else // Meh, possibly not botting... reset.
						_lastKillLocations.remove(player.getObjectId());
				}
			}
			
			if (doCaptcha)
			{	
				// Safe check for pet.
				if (player.getPet() != null)
				{
					player.getPet().abortAttack(true, true);
					player.getPet().abortCast(true, true);
				}
				
				// Safe check to prevent double captcha.
				player.abortAttack(true, true);
				player.abortCast(true, true);
				
				// Random image file name
				int imgId = IdFactory.getInstance().getNextId();
				try
				{
					// Increase captcha requests.
					increaseCaptchaRequests(player);
					newCaptchaCode(player);
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(CaptchaImage.generateCaptcha(getCaptchaCode(player)), "png", baos);
					baos.flush();
					ByteBuffer buffer = DDSConverter.convertToDDS(ByteBuffer.wrap(baos.toByteArray()), "lol/png");
					player.sendPacket(new PledgeCrest(imgId, buffer.array()));
					baos.close();
					
					player.startAbnormalEffect(AbnormalEffect.REAL_TARGET);
					if (!player.isParalyzed())
						player.startParalyzed();
					player.setIsInvul(true);
					if (!player.isBlocked())
						player.block();
					
					starTasksPunishment(player);
					starTasksMessage(player);
					
					NpcHtmlMessage html = new NpcHtmlMessage(0);
					html.setHtml(customHtm);
					html.replace("%imgId%", "" + imgId);
					html.replace("%serverId%", "" + player.getServerId());
					html.replace("%playerName%", "" + player.getName());
					html.replace("%tries%", "" + getCaptchaTries(player));
					html.replace("%punishmentType%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE);
					html.replace("%punishmentTime%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME);
					player.sendPacket(html);
				}
				catch (Exception e)
				{
					_log.error("", e);
				}
			}

		}
	}
	
	public static long getCaptchaRequests(Player player)
	{
		return ((_captchaPlayers.get(player.getObjectId()) >> CAPTCHA_REQUESTS) & 0x1F);
	}
	
	public static void increaseCaptchaRequests(Player player)
	{
		long fullVal = _captchaPlayers.get(player.getObjectId());
		long requests = ((fullVal >> CAPTCHA_REQUESTS) & 0x1F); // Shifts to the right, then marks the last 5 bits and deletes the rest.
		fullVal &= ~(0x1F << CAPTCHA_REQUESTS); // creates 5 bits all set to 1, then shifts them to the left, then reverts them to 0s and all 0s to 1s, so you get a long value consisting only of 1s except for the place of the 5 bits. Wherever the location of those 5 bits is, all data there will be erased (set to 0s).
		fullVal |= ++requests << CAPTCHA_REQUESTS; // gets the binary of the number of requests++ and then shifts them to the left. It results in a long which consists of 0s except for the bits of the small number we have. On their location they will replace everything with their binary code.
		_captchaPlayers.put(player.getObjectId(), fullVal);
	}
	
	public static long getCaptchaTries(Player player)
	{
		long fullVal = _captchaPlayers.get(player.getObjectId());
		boolean initializeCaptchaTries = ((fullVal >> (CAPTCHA_TRIES + 4)) & 0x1L) == 0;
		if (initializeCaptchaTries)
		{
			fullVal |= 1L << (CAPTCHA_TRIES + 4); // It just sets the bit which is 33+4 to the left to 1.
			fullVal &= ~(0xFL << CAPTCHA_TRIES); // Makes 4 bit mask and erases the given location (sets 4 bits to 0).
			fullVal |= (long)(Config.CAPTCHA_ATTEMPTS) << CAPTCHA_TRIES; //Gets the binary value of CAPTCHA_ATTEMPTS and places it on the given location. It works only if the given location is all 0s, which we've done with the previous code.
			_captchaPlayers.put(player.getObjectId(), fullVal);
			return Config.CAPTCHA_ATTEMPTS;
		}
		
		return (fullVal >> CAPTCHA_TRIES) & 0xFL;
	}
	
	public static void resetCaptchaTries(Player player)
	{
		long fullVal = _captchaPlayers.get(player.getObjectId());
		fullVal &= ~(0x1F << CAPTCHA_TRIES); // Erases 5 bits at the given location.
		_captchaPlayers.put(player.getObjectId(), fullVal);
	}
	
	public static void reduceCaptchaTries(Player player)
	{
		long tries = getCaptchaTries(player);
		long fullVal = _captchaPlayers.get(player.getObjectId());
		fullVal &= ~(0xFL << CAPTCHA_TRIES); // Erases 4 bits at the given location
		fullVal |= --tries << CAPTCHA_TRIES; // Sets the erased 4 bits with the value of "tries"
		_captchaPlayers.put(player.getObjectId(), fullVal);
	}
	
	public static void newCaptchaCode(Player player)
	{
		char[] code = new char[5];
		for (int i = 0; i < code.length; i++)
			code[i] = ELIGIBLE_CHARS[Rnd.get(ELIGIBLE_CHARS.length)];
		
		long fullVal = _captchaPlayers.get(player.getObjectId());
		for (byte i=0; i < code.length; i++)
		{
			fullVal &= ~(0x1FL << CAPTCHA_CODE + i*5);
			fullVal |= (((long)(code[i] - 64)) << CAPTCHA_CODE + i*5);
		}
		
		fullVal |= 1L << CAPTCHA_WRONG; // Just making the leftest bit to 1.
		_captchaPlayers.put(player.getObjectId(), fullVal);
		
		if (player.isGM())
			player.sendMessage("Captcha Code: " + String.valueOf(code));
	}
	
	public static char[] getCaptchaCode(Player player)
	{
		long fullVal = _captchaPlayers.get(player.getObjectId());
		char[] code = new char[5];
		
		for (byte i=0; i < 5; i++)
			code[i] = ((char)(64+((fullVal >> CAPTCHA_CODE + i*5) & 0x1FL)));
		
		return code;
	}
	
	public static void resetCaptcha(Player player)
	{
		_captchaPlayers.remove(player.getObjectId());
		_lastKillLocations.remove(player.getObjectId());
	}
	
	public static boolean isCaptchaCodeWrong(Player player)
	{
		return _captchaPlayers.get(player.getObjectId()) >> CAPTCHA_WRONG != 0;
	}
	
	public static void addKilledMob(Player player)
	{
		long currentMobKills = _captchaPlayers.get(player.getObjectId());
		_captchaPlayers.put(player.getObjectId(), ++currentMobKills);
	}
	
	public static int getKilledMobs(int objId)
	{
		int kills = (int) _captchaPlayers.get(objId);
		kills &= 0xFFFFFFF;
		return kills;
	}
	
	public static void starTasksMessage(Player player)
	{
		endMessageTask();
		_startTaskMessage = ThreadPoolManager.getInstance().schedule(new startMessageTimer(player), 5000);
	}
	
	public static void endMessageTask()
	{
		if(_startTaskMessage != null)
			_startTaskMessage.cancel(true);
		_startTaskMessage = null;
	}
	
	public static void starTasksPunishment(Player player)
	{
		endPunishTask();
		_startTaskPunishment = ThreadPoolManager.getInstance().schedule(new CaptchaTimer(player), 180000);
	}
	
	public static void starTasksPunishment(Player player, boolean fromReport)
	{
		endPunishTask();
		_startTaskPunishment = ThreadPoolManager.getInstance().schedule(new CaptchaTimer(player, fromReport), 180000);
	}
	
	public static void endPunishTask()
	{
		if(_startTaskPunishment != null)
			_startTaskPunishment.cancel(true);
		_startTaskPunishment = null;
	}
	
	private static class CaptchaTimer implements Runnable
	{
		private final Player activeChar;
		private final boolean _fromReport;
		
		
		private CaptchaTimer(Player player)
		{
			activeChar = player;
			_fromReport = false;
		}
		
		private CaptchaTimer(Player player, boolean fromReport)
		{
			activeChar = player;
			_fromReport = fromReport;
		}
		
		@Override
		public void run()
		{
			// here will be code that will run after 1 min
			if (isCaptchaCodeWrong(activeChar))
			{
				String customHtm = HtmCache.getInstance().getNotNull("mods/Captcha/TimeEnded.htm", activeChar);
				
				resetCaptchaTries(activeChar);
				// here will run method with jailing player after 1 min
				activeChar.stopAbnormalEffect(AbnormalEffect.REAL_TARGET);
				if (activeChar.isFlying())
					activeChar.setTransformation(0);
				
				activeChar.setIsInvul(false);
				activeChar.stopParalyzed();
				activeChar.unblock();
				
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setHtml(customHtm);
				html.replace("%playerName%", "" + activeChar.getName());
				html.replace("%serverId%", "" + activeChar.getServerId());
				html.replace("%tries%", "" + getCaptchaTries(activeChar));
				html.replace("%punishmentType%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE);
				html.replace("%punishmentTime%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME);
				activeChar.sendPacket(html);
				
				endPunishTask();
				endMessageTask();
				
				_log.info("Player " + activeChar.getName() + " has failed on captcha and has been " + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE);
				
				// Reset captcha requests.
				resetCaptcha(activeChar);
				
				Log.bots("Captcha: " + activeChar.getName() + " failed on captcha-test [TIMES UP] and will be recive punishment: " + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE);
				
				switch(Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE)
				{
					case "KICK":
					{
						if (activeChar != null)
						{
							activeChar.sendMessage("You have failed on captcha, so the server will kick you. Cya :)");
							activeChar.kick();
						}
						else
							_log.warn("Captcha System: Something went wrong, player is null...");
						break;
					}
					case "BANCHAR":
					{
						int punishTime = Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME;
						if (punishTime != 0 && activeChar != null)
						{
							activeChar.sendMessage(new CustomMessage("admincommandhandlers.YoureBannedByGM", activeChar));
							AutoBan.Banned(activeChar.getName(), -100, punishTime, "Failed on Captcha", activeChar.getName());
							activeChar.kick();
						}
						else
							_log.warn("Captcha System: Something went wrong, player is null or time is null");
						break;
					}
					case "JAIL":
					{
						int punishTime = Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME;
						if (punishTime != 0 && activeChar != null)
						{
							AutoBan.Jail(activeChar.getName(), punishTime, "Failed on Captcha", null);
						}
						else
							_log.warn("Captcha System: Something went wrong, player is null or time is null");
						break;
					}
				}
				
				if (_fromReport)
					AdminTable.broadcastToGMs(new Say2(0, ChatType.HERO_VOICE, "REPORT", activeChar.getName() + "  has FAILED on captcha test!"));
				
				activeChar.setVar("FailedOnCaptchaTest", activeChar.getVarInt("FailedOnCaptchaTest", 0) + 1);
			}
		}
	}
	
	private static class startMessageTimer implements Runnable
	{
		Player activeChar;
		
		public startMessageTimer(Player player)
		{
			activeChar = player;
		}
		
		@Override
		public void run()
		{
			int time = 180; // 180 sec=  3 minutes time for task
			
			while (time >= 0)
			{
				if (_startTaskMessage == null || _startTaskMessage.isCancelled())
					break;
				
				int sec = time - time / 60 * 60;
				
				String message = "";
				
				if (sec < 10)
					message = message + "\nTime left " + time / 60 + ":0" + sec + " to enter captcha code.";
				else
					message = message + "\nTime left " + time / 60 + ":" + sec + " to enter captcha code.";
				
				activeChar.sendPacket(new ExShowScreenMessage(message, 1000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false));
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{

				}
				
				time--;
			}
		}
	}
	
	public static void requestAnotherCaptcha(Player player)
	{
		String customHtm = HtmCache.getInstance().getNotNull("mods/Captcha/ShowCaptchaWindow.htm", player);
		
		//Random image file name
		int imgId = IdFactory.getInstance().getNextId();
		try
		{
			File captcha = new File("data/images/10000.png");    
			ImageIO.write(CaptchaImage.generateCaptcha(getCaptchaCode(player)), "png", captcha);
			player.sendPacket(new PledgeCrest(imgId, DDSConverter.convertToDDS(captcha).array()));
		}
		catch (Exception e)
		{
			_log.error("", e);
		}
		
		newCaptchaCode(player);
		
		// Increase captcha requests.
		increaseCaptchaRequests(player);
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(customHtm);
		html.replace("%imgId%", "" + imgId);
		html.replace("%playerName%", "" + player.getName());
		html.replace("%tries%", "" + getCaptchaTries(player));
		html.replace("%serverId%", "" + player.getServerId());
		html.replace("%punishmentType%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TYPE);
		html.replace("%punishmentTime%", "" + Config.CAPTCHA_FAILED_CAPTCHA_PUNISHMENT_TIME);
		player.sendPacket(html);
	}
	
	private static Color randomColor()
	{
		Random random = new Random();
		
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}
	
	public static BufferedImage generateCaptcha(char[] code)
	{
		Color textColor = new Color(98, 213, 43);
		//Color circleColor = new Color(98, 213, 43);
		Font textFont = new Font("comic sans ms", Font.BOLD, 24);
		int charsToPrint = 5; // how many characters..
		int width = 256;
		int height = 64;
		int circlesToDraw = 4;
		int linesToDraw = 2;
		float horizMargin = 20.0f;
		double rotationRange = 0.7; // this is radians
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		
		// Draw an oval
		g.setColor(new Color(30, 31, 31));
		g.fillRect(0, 0, width, height);
		
		// lets make some noisey circles
		g.setColor(randomColor());
		for (int i = 0; i < circlesToDraw; i++)
		{
			int circleRadius = (int) (Math.random() * height / 2.0);
			int circleX = (int) (Math.random() * width - circleRadius);
			int circleY = (int) (Math.random() * height - circleRadius);
			g.drawOval(circleX, circleY, circleRadius * 2, circleRadius * 2);
		}
		
		// Draw Lines too..
		for (int i = 0; i < linesToDraw; i++)
		{
			// unless divided by some factor, these lines were being
			// drawn outside the bound of the image..
			int lineRadius = (int) (Math.random() * height / 2.0);
			int lineRadiusX = (int) (Math.random() * width - lineRadius);
			int lineRadiusY = (int) (Math.random() * height - lineRadius);
			g.drawLine(lineRadiusX, lineRadiusY, lineRadius * 2, lineRadius * 2);
		}

		g.setColor(randomColor());
		g.setFont(textFont);
		//g.setColor(textColor);
		//g.setFont(textFont);
		
		FontMetrics fontMetrics = g.getFontMetrics();
		int maxAdvance = fontMetrics.getMaxAdvance();
		int fontHeight = fontMetrics.getHeight();
		
		// Suggestions ----------------------------------------------------------------------
		// i removed 1 and l and i because there are confusing to users...
		// Z, z, and N also get confusing when rotated
		// 0, O, and o are also confusing...
		// lowercase G looks a lot like a 9 so i killed it
		// this should ideally be done for every language...
		// i like controlling the characters though because it helps prevent confusion
		// So recommended chars are:
		// String elegibleChars = "ABCDEFGHJKLMPQRSTUVWXYabcdefhjkmnpqrstuvwxy23456789";
		// Suggestions ----------------------------------------------------------------------
		
		float spaceForLetters = -horizMargin * 2 + width;
		float spacePerChar = spaceForLetters / (charsToPrint - 1.0f);
		for (int i = 0; i < charsToPrint; i++)
		{
			// this is a separate canvas used for the character so that
			// we can rotate it independently
			int charWidth = fontMetrics.charWidth(code[i]);
			int charDim = Math.max(maxAdvance, fontHeight);
			int halfCharDim = (charDim / 2);
			
			BufferedImage charImage = new BufferedImage(charDim, charDim, BufferedImage.TYPE_INT_ARGB);
			Graphics2D charGraphics = charImage.createGraphics();
			charGraphics.translate(halfCharDim, halfCharDim);
			double angle = (Math.random() - 0.5) * rotationRange;
			charGraphics.transform(AffineTransform.getRotateInstance(angle));
			charGraphics.translate(-halfCharDim, -halfCharDim);
			charGraphics.setColor(textColor);
			charGraphics.setFont(textFont);
			
			int charX = (int) (0.5 * charDim - 0.5 * charWidth);
			charGraphics.drawString("" + code[i], charX, ((charDim - fontMetrics.getAscent()) / 2 + fontMetrics.getAscent()));
			
			float x = horizMargin + spacePerChar * (i) - charDim / 2.0f;
			int y = ((height - charDim) / 2);
			g.drawImage(charImage, (int) x, y, charDim, charDim, null, null);
			
			charGraphics.dispose();
		}
		
		g.dispose();
		
		return bufferedImage;
	}
}
