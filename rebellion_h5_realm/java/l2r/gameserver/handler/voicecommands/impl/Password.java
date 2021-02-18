package l2r.gameserver.handler.voicecommands.impl;

import l2r.gameserver.Config;
import l2r.gameserver.data.htm.HtmCache;
import l2r.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2r.gameserver.model.Player;
import l2r.gameserver.network.loginservercon.AuthServerCommunication;
import l2r.gameserver.network.loginservercon.gspackets.ChangePassword;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.components.ChatType;
import l2r.gameserver.network.serverpackets.components.CustomMessage;
import l2r.gameserver.scripts.Functions;
import l2r.gameserver.utils.Log;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class Password extends Functions implements IVoicedCommandHandler
{
	private static Session SESSION = null;
	static
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", Config.SMTP_SERVER);
		props.put("mail.smtp.port", Config.SMTP_SERVER_PORT);
		props.put("mail.smtp.auth", Config.SMTP_SERVER_AUTH);
		props.put("mail.smtp.timeout", Config.SMTP_SERVER_TIMEOUT);
		props.put("mail.smtp.connectiontimeout", Config.SMTP_SERVER_CONNECTION_TIMEOUT);

		switch (Config.SMTP_SERVER_SECUIRTY)
		{
			case "TLS":
			{
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.socketFactory.port", Config.SMTP_SERVER_PORT);
				break;
			}
			case "SSL":
			{
				props.put("mail.smtp.ssl", "true");
				props.put("mail.smtp.socketFactory.port", Config.SMTP_SERVER_PORT);
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				break;
			}
		}

		if (Config.SMTP_SERVER_AUTH)
		{
			SESSION = Session.getDefaultInstance(props, new javax.mail.Authenticator()
			{
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(Config.SMTP_USERNAME, Config.SMTP_PASSWORD);
				}
			});
		}
	}
	
	private String[] _commandList = new String[]
	{
		"password",
		"changepassword",
		"check"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if (!Config.ENABLE_PASSWORD_COMMAND)
			return false;
		
		if (Config.SECURITY_ENABLED && activeChar.getSecurity())
		{
			activeChar.sendChatMessage(0, ChatType.TELL.ordinal(), "SECURITY", (activeChar.isLangRus() ? "Для того, чтобы это сделать, идентифицировать себя с помощью .security" : "In order to do this, identify yourself via .security"));
			return false;
		}
		
		command = command.intern();
		if (command.equalsIgnoreCase("password") || command.equalsIgnoreCase("changepassword"))
			return password(command, activeChar, args);
		if (command.equalsIgnoreCase("check"))
			return check(command, activeChar, args);
		
		return false;
		
	}
	
	private boolean password(String command, Player activeChar, String target)
	{
		if (command.equals("password") || command.equals("changepassword"))
		{
			String dialog = HtmCache.getInstance().getNotNull("command/password.htm", activeChar);
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
			npcHtmlMessage.setHtml(dialog);
			activeChar.sendPacket(npcHtmlMessage);
			return true;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
	
	private boolean check(String command, Player activeChar, String target)
	{
		String[] parts = target.split(" ");
		
		if (parts.length != 3)
		{
			show(new CustomMessage("scripts.commands.user.password.IncorrectValues", activeChar), activeChar);
			return false;
		}
		
		if (!parts[1].equals(parts[2]))
		{
			show(new CustomMessage("scripts.commands.user.password.IncorrectConfirmation", activeChar), activeChar);
			return false;
		}
		
		if (parts[1].equals(parts[0]))
		{
			show(new CustomMessage("scripts.commands.user.password.NewPassIsOldPass", activeChar), activeChar);
			return false;
		}
		
		if (parts[1].length() < 4 || parts[1].length() > 16)
		{
			show(new CustomMessage("scripts.commands.user.password.IncorrectSize", activeChar), activeChar);
			return false;
		}
		
		
		AuthServerCommunication.getInstance().sendPacket(new ChangePassword(activeChar.getAccountName(), parts[0], parts[1], "null"));
		show(new CustomMessage("scripts.commands.user.password.ResultTrue", activeChar), activeChar);
		Log.addGame("Player " + activeChar.getName() + " with account: " + activeChar.getAccountName() + " IP: " + activeChar.getClient().getIpAddr() + " has changed his password from ( " + parts[0] + " ) to ( " + parts[1] +  " )", "ChangePassword");
		
		return true;
	}
}