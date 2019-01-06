
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Setting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TestImportEvent {
	private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		// TODO Auto-generated method stub
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName("APPLICATION_NAME").build();

        // Create and initialize a new event (could also retrieve an existing event)
	    // Per importare un evento gia creato in un altra data senza dover scrivere tutto
		
		Event event = service.events().get("primary", "kl4bjrpj7r487j4dr21dbgoruc").execute();
	    
        
		Event.Organizer organizer = new Event.Organizer();
		organizer.setEmail("gabypana1986@gmail.com");
		organizer.setDisplayName("GabrielP");
		event.setOrganizer(organizer);

		ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
		attendees.add(new EventAttendee().setEmail("florin.dan.ivan@gmail.com"));

		event.setAttendees(attendees);

		DateTime startDateTime = new DateTime("2019-12-31T09:00:00");
		EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("Europe/Rome");
		event.setStart(start);

		DateTime endDateTime = new DateTime("2019-12-31T12:00:00");
		EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("Europe/Rome");
		event.setEnd(end);

// Import the event into a calendar
		Event importedEvent = service.events().calendarImport("primary", event).execute();

		System.out.println(importedEvent.getId()+ " -> " + importedEvent.getDescription());
	}
}
