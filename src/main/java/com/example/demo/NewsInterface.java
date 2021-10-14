
package com.example.demo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.EventListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.annotations.Nullable;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Configuration
@SpringBootApplication
@RestController
public class NewsInterface {

	static FirebaseApp app;
	static Firestore db;
	static Map<String, News> contents;

//	private static final String[] CLASSPATH_RESOURCE_LOCATIONS =
//			{
//					"classpath:/META-INF/resources/",
//					"classpath:/resources/",
//					"classpath:/static/",
//					"classpath:/public/",
//					"classpath:/custom/",
//					"file:/opt/myfiles/"
//			};
//
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry)
//	{
//		registry.addResourceHandler("/**")
//				.addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
//				.setCachePeriod(3000);
//	}

	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
		FileInputStream refreshToken = new FileInputStream("./services.json");
		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(refreshToken))
				.setDatabaseUrl("https://esdiacapp-59676-default-rtdb.firebaseio.com/")
				.setProjectId("esdiacapp-59676")
				.build();

		app = FirebaseApp.initializeApp(options);
		db = FirestoreClient.getFirestore();

		contents = new HashMap<String, News>();
		loadInformation();
		SpringApplication.run(NewsInterface.class, args);
	}

	@GetMapping(
			value = "/homeimage",
			produces = MediaType.IMAGE_JPEG_VALUE
	)
	public @ResponseBody byte[] getHomeImage() throws IOException {
		InputStream in = getClass()
				.getResourceAsStream("/com/example/demo/home.png");
		return in.readAllBytes();
	}
	@GetMapping(
			value = "/logoutimage",
			produces = MediaType.IMAGE_JPEG_VALUE
	)
	public @ResponseBody byte[] getLogoutImage() throws IOException {
		InputStream in = getClass()
				.getResourceAsStream("/com/example/demo/logout.png");
		return in.readAllBytes();
	}
	@GetMapping(
			value = "/logowhiteimage",
			produces = MediaType.IMAGE_JPEG_VALUE
	)
	public @ResponseBody byte[] getLogoWhiteImage() throws IOException {
		InputStream in = getClass()
				.getResourceAsStream("/com/example/demo/logo_white.png");
		return in.readAllBytes();
	}
	@GetMapping(
			value = "/dashboardimage",
			produces = MediaType.IMAGE_JPEG_VALUE
	)
	public @ResponseBody byte[] getDashboardImage() throws IOException {
		InputStream in = getClass()
				.getResourceAsStream("/com/example/demo/dashboard.png");
		return in.readAllBytes();
	}

	public static void loadInformation() {

		Query query = db.collection("test-news")
				.whereNotEqualTo("id", null)
				.orderBy("id");
		query.addSnapshotListener(new EventListener<QuerySnapshot>() {
			@SneakyThrows
			@Override
			public void onEvent(@Nullable QuerySnapshot snapshots,
								@Nullable FirestoreException e) {

				for(DocumentChange snap : snapshots.getDocumentChanges()){
					if(snap.getType() == DocumentChange.Type.REMOVED){
						contents.remove(snap.getDocument().getString("id"));
					}
					else {
						contents.put(snap.getDocument().getString("id"),(new News(snap.getDocument().getString("id"),snap.getDocument().getString("title"),snap.getDocument().getString("description"),snap.getDocument().getString("image"),snap.getDocument().getString("video"))));
					}
				}
			}
		});
	}
}


