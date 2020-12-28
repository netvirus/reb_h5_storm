package l2r.gameserver.utils;

import l2r.gameserver.GameTimeController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils
{
	private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat DATE_HOUR_FORMAT = new SimpleDateFormat("HH:mm dd/MM/yyyy");

	public static String toSimpleFormat(Calendar cal)
	{
		return DATE_HOUR_FORMAT.format(cal.getTime());
	}

	public static String convertDateToString(long time)
	{
		Date dt = new Date(time);
		String stringDate = DATE_HOUR_FORMAT.format(dt);
		return stringDate;
	}
	
	public static long getMillisecondsFromString(String datetime)
	{
		return getMillisecondsFromString(datetime, "dd/MM/yyyy HH:mm");
	}
	
	public static long getMillisecondsFromString(String datetime, String format)
	{
		DateFormat df = new SimpleDateFormat(format); 
		try
		{ 
			Date time = df.parse(datetime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);

			return calendar.getTimeInMillis();
		} 
		catch(Exception e) 
		{ 
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public static String convertDateToString(long time, boolean onlyHour, boolean onlyDate)
	{
		Date dt = new Date(time);
		String stringDate = DATE_HOUR_FORMAT.format(dt);
		
		if (onlyHour)
			stringDate = HOUR_FORMAT.format(dt);
		
		if (onlyDate)
			stringDate = DATE_FORMAT.format(dt);
		
		return stringDate;
	}
	
	public static String toSimpleFormat(long cal)
	{
		return DATE_HOUR_FORMAT.format(cal);
	}

	public static String getDateString(Date date)
	{
		return DATE_HOUR_FORMAT.format(date.getTime());
	}
	
	public static String minutesToFullString(int period)
	{
		StringBuilder sb = new StringBuilder();

		// парсим дни
		if(period > 1440) // больше 1 суток
		{
			sb.append((period - (period % 1440)) / 1440).append(" д.");
			period = period % 1440;
		}

		// парсим часы
		if(period > 60) // остаток более 1 часа
		{
			if(sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append((period - (period % 60)) / 60).append(" ч.");

			period = period % 60;
		}

		// парсим остаток
		if(period > 0) // есть остаток
		{
			if(sb.length() > 0)
			{
				sb.append(", ");
			}

			sb.append(period).append(" мин.");
		}
		if(sb.length() < 1)
		{
			sb.append("меньше 1 мин.");
		}

		return sb.toString();
	}
	
	public static String minutesToFullString(int period, boolean FullString, boolean days, boolean hours, boolean minutes)
	{
		StringBuilder sb = new StringBuilder();

		// парсим дни
		if(period > 1440 && days) // больше 1 суток
		{
			if (FullString)
				sb.append((period - (period % 1440)) / 1440).append(" день(й)");
			else
				sb.append((period - (period % 1440)) / 1440).append(" д.");
			period = period % 1440;
		}

		// парсим часы
		if(period > 60 && hours) // остаток более 1 часа
		{
			if(sb.length() > 0)
			{
				sb.append(", ");
			}

			if (FullString)
				sb.append((period - (period % 60)) / 60).append(" час(ов)");
			else
				sb.append((period - (period % 60)) / 60).append(" ч.");

			period = period % 60;
		}

		// парсим остаток
		if(period > 0 && minutes) // есть остаток
		{
			if(sb.length() > 0)
			{
				sb.append(", ");
			}

			if (FullString)
				sb.append(period).append(" минут(ы)");
			else
				sb.append(period).append(" мин.");
		}
		
		if(sb.length() < 1)
		{
			sb.append("меньше 1 мин.");
		}

		return sb.toString();
	}
	
	public static String getConvertedTime(long seconds)
	{
		int days = (int) (seconds / 86400);
		seconds -= days * 86400;
		int hours = (int) (seconds / 3600);
		seconds -= hours * 3600;
		int minutes = (int) (seconds / 60);
		
		boolean includeNext = true;
		String time = "";
		if (days > 0)
		{
			time = days + " Day(s) ";
			if (days > 5)
				includeNext = false;
		}
		if (hours > 0 && includeNext)
		{
			if (time.length() > 0)
				includeNext = false;
			time += hours + " Hour(s) ";
			if (hours > 10)
				includeNext = false;
		}
		if (minutes > 0 && includeNext)
		{
			time += minutes + " Min(s)";
		}
		return time;
	}
	
	public static String getTimeInServer()
	{
		String hour, minute;
		int h = GameTimeController.getInstance().getGameHour();
		int m = GameTimeController.getInstance().getGameMin();
		
		
		String type;
		if (GameTimeController.getInstance().isNowNight())
			type = "Night";
		else
			type = "Day";
		
		if (h < 10)
			hour = "0" + h;
		else
			hour = "" + h;
		
		if (m < 10)
			minute = "0" + m;
		
		else
			minute = "" + m;
		
		String time = hour + ":" + minute + " (" + type + ")";
		return time;
	}

	public static long getMilisecondsToNextDay(List<Integer> days, int hourOfTheEvent)
	{
		return getMilisecondsToNextDay(days, hourOfTheEvent, 5);
	}

	public static long getMilisecondsToNextDay(List<Integer> days, int hourOfTheEvent, int minuteOfTheEvent)
	{
		int[] hours = new int[days.size()];
		for (int i = 0; i< hours.length; i++)
			hours[i] = days.get(i).intValue();
		return getMilisecondsToNextDay(hours, hourOfTheEvent, minuteOfTheEvent);
	}

	/**
	 * Getting Time in Milliseconds to the closest day.
	 * If every day already passed, it's getting closest day of next month
	 * Event Time: Millisecond: 0, Second: 0, Minute: 0, Hour: hourOfTheEvent
	 * @param days Array of specific days in the month
	 * @param hourOfTheEvent hour of the day, when clock will stop
	 * @param minuteOfTheEvent
	 * @return Time in milliseconds to that day
	 */
	public static long getMilisecondsToNextDay(int[] days, int hourOfTheEvent, int minuteOfTheEvent)
	{
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(Calendar.SECOND, 0);
		tempCalendar.set(Calendar.MILLISECOND, 0);
		tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfTheEvent);
		tempCalendar.set(Calendar.MINUTE, minuteOfTheEvent);

		final long currentTime = System.currentTimeMillis();
		Calendar eventCalendar = Calendar.getInstance();

		boolean found = false;
		long smallest = Long.MAX_VALUE;

		for (int day : days)
		{
			tempCalendar.set(Calendar.DAY_OF_MONTH, day);
			long timeInMillis = tempCalendar.getTimeInMillis();

			if (timeInMillis <= currentTime)
			{
				if (timeInMillis < smallest)
					smallest = timeInMillis;
				continue;
			}

			if (!found || timeInMillis < eventCalendar.getTimeInMillis())
			{
				found = true;
				eventCalendar.setTimeInMillis(timeInMillis);
			}
		}

		if (!found)
		{
			eventCalendar.setTimeInMillis(smallest);
			eventCalendar.add(Calendar.MONTH, 1);
		}
		return eventCalendar.getTimeInMillis() - currentTime;
	}

	public static long addDay(int count) { return (count * 60 * 60 * 24 * 1000L); }

	public static long addHours(int count) { return (count * 60 * 60 * 1000L); }

	public static long addMinutes(int count) { return (count * 60 * 1000L); }

	public static long addSecond(int count) { return (count * 1000L); }

	public static String formatTime(int time) { return formatTime(time, true); }

	public static String formatTime(int time, boolean cut)
	{
		int days = time / 86400;
		int hours = (time - days * 24 * 3600) / 3600;
		int minutes = (time - days * 24 * 3600 - hours * 3600) / 60;

		String result;

		if (days >= 1)
		{
			if ((hours < 1) || (cut))
			{
				result = days + " " + declension(days, TimeUtilDeclension.DAYS);
			}
			else
			{
				result = days + " " + declension(days, TimeUtilDeclension.DAYS) + " " + hours + " " + declension(hours, TimeUtilDeclension.HOUR);
			}
		}
		else
		{
			if (hours >= 1)
			{
				if ((minutes < 1) || (cut))
				{
					result = hours + " " + declension(hours, TimeUtilDeclension.HOUR);
				}
				else
				{
					result = hours + " " + declension(hours, TimeUtilDeclension.HOUR) + " " + minutes + " " + declension(minutes, TimeUtilDeclension.MINUTES);
				}
			}
			else
			{
				result = minutes + " " + declension(minutes, TimeUtilDeclension.MINUTES);
			}
		}
		return result;
	}

	public static String declension(long count, TimeUtilDeclension word)
	{
		String one = "";
		String two = "";
		String five = "";
		switch (word)
		{
			case DAYS:
				one = new String("Day");
				two = new String("Days");
				five = new String("Days");
				break;
			case HOUR:
				one = new String("Hour");
				two = new String("Hours");
				five = new String("Hours");
				break;
			case MINUTES:
				one = new String("Minute");
				two = new String("Minutes");
				five = new String("Minutes");
				break;
		}
		if (count > 100L)
		{
			count %= 100L;
		}
		if (count > 20L)
		{
			count %= 10L;
		}
		if (count == 1L)
		{
			return one;
		}
		if ((count == 2L) || (count == 3L) || (count == 4L))
		{
			return two;
		}
		return five;
	}

	public static long getMillisecondsFromDaysHoursMinutes(int days, int hours, int minutes)
	{
		long duration = 0;
		if (days > 0)
		{
			duration += days * 86400;
		}

		if (hours > 0)
		{
			duration += hours * 3600;
		}

		if (minutes > 0)
		{
			duration += minutes * 60;
		}
		return duration;
	}
}
