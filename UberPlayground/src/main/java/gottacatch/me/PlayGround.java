package gottacatch.me;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.google.api.client.auth.oauth2.Credential;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.auth.OAuth2Credentials.Scope;
import com.uber.sdk.rides.client.Response;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.Session.Environment;
import com.uber.sdk.rides.client.UberRidesServices;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.error.ApiException;
import com.uber.sdk.rides.client.error.NetworkException;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.RideRequestParameters.Builder;
import com.uber.sdk.rides.client.model.TimeEstimate;
import com.uber.sdk.rides.client.model.TimeEstimatesResponse;

public class PlayGround {

	private final AppProperties appProperties;

	// TNW Hack Battle location (approximately).
	private static final Location TNW = new Location(52.386787f, 4.872902f);

	// Randolf's home location (approximately).
	private static final Location HOME = new Location(52.409927f, 4.901676f);

	private static final String OAUTH2_CALLBACK_URL = "https://www.getpostman.com/oauth2/callback";

	private PlayGround() {
		appProperties = new AppProperties(this.getClass().getResourceAsStream("app.properties"));
	}

	public static void main(final String[] arguments) {
		new PlayGround().test();
	}

	private void test() {
		try {
			showSomeInfoUsingApp(TNW);

			final Collection<Scope> scopes = new ArrayList<Scope>();
			scopes.add(Scope.PROFILE);
			scopes.add(Scope.REQUEST);
			final OAuth2Credentials credentials = new OAuth2Credentials.Builder()
					.setClientSecrets(appProperties.getClientId(), appProperties.getClientSecret())
					.setRedirectUri(OAUTH2_CALLBACK_URL).setScopes(scopes).build();

			final String authorizationUrl = credentials.getAuthorizationUrl();

			System.out.println("Execute this URL in your browser: " + authorizationUrl);

			final String authorizationCode = getAuthorizationCode();

			final Credential credential = credentials.authenticate(authorizationCode, appProperties.getUser());

			final Session session = new Session.Builder().setCredential(credential).setEnvironment(Environment.SANDBOX)
					.build();

			issueAndCancelRideRequest(session, TNW, HOME);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static String getAuthorizationCode() throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Paste the value of name 'code' here: ");
		return br.readLine();
	}

	private void showSomeInfoUsingApp(final Location pickupLocation) throws ApiException, NetworkException {
		final Session session = new Session.Builder().setServerToken(appProperties.getServerToken())
				.setEnvironment(Environment.SANDBOX).build();
		final UberRidesSyncService service = UberRidesServices.createSync(session);
		final List<Product> products = service.getProducts(pickupLocation.getLatitude(), pickupLocation.getLongitude())
				.getBody().getProducts();

		System.out.println("Available products:");
		for (final Product product : products) {
			showProductInfo(service, product, null, pickupLocation);
		}

		final Product product = pickProduct(products);

		System.out.println("Picked product productId: " + product.getProductId());
	}

	private static void issueAndCancelRideRequest(final Session session,
			final Location pickupLocation,
			final Location dropoffLocation) throws ApiException, NetworkException {
		final UberRidesSyncService service = UberRidesServices.createSync(session);
		final Builder rideRequestParametersBuilder = new RideRequestParameters.Builder()
				.setPickupCoordinates(pickupLocation.getLatitude(), pickupLocation.getLongitude())
				.setDropoffCoordinates(dropoffLocation.getLatitude(), dropoffLocation.getLongitude());

		final List<Product> products = service.getProducts(pickupLocation.getLatitude(), pickupLocation.getLongitude())
				.getBody().getProducts();

		System.out.println("Available products:");
		for (final Product product : products) {
			showProductInfo(service, product, rideRequestParametersBuilder, pickupLocation);
		}

		final Product product = pickProduct(products);

		System.out.println("Picked product productId: " + product.getProductId());

		final Ride ride = service.requestRide(rideRequestParametersBuilder.setProductId(product.getProductId()).build())
				.getBody();
		final String rideId = ride.getRideId();
		System.out.println("RideId: " + rideId);

		System.out.println("Status: " + ride.getStatus());

		System.out.println("Cancelling ride...");
		final Response<Void> cancelRideResponse = service.cancelRide(rideId);
		System.out.println("CancelRide status: " + cancelRideResponse.getStatus());
		System.out.println("CancelRide reason: " + cancelRideResponse.getReason());

		System.out.println("Status: " + ride.getStatus());
	}

	private static void showProductInfo(final UberRidesSyncService service,
			final Product product,
			final Builder rideRequestParametersBuilder,
			final Location pickupLocation) throws ApiException, NetworkException {
		final TimeEstimatesResponse etaResponse = service.getPickupTimeEstimates(pickupLocation.getLatitude(),
				pickupLocation.getLongitude(), product.getProductId()).getBody();
		System.out.println("DisplayName: " + product.getDisplayName());
		System.out.println("{");
		System.out.println("\tDescription: " + product.getDescription());
		System.out.println("\tProductId: " + product.getProductId());
		System.out.println("\tCapacity: " + product.getCapacity());
		System.out.println("\tImage: " + product.getImage());
		for (final TimeEstimate eta : etaResponse.getTimes()) {
			System.out.println("\tETA in seconds: " + eta.getEstimate());
		}
		if (rideRequestParametersBuilder != null) {
			final RideEstimate estimateRideResponse = service
					.estimateRide(rideRequestParametersBuilder.setProductId(product.getProductId()).build()).getBody();
			System.out.println("\tPickupEstimate in minutes: " + estimateRideResponse.getPickupEstimate());
			System.out.println("\tPrice (Display): " + estimateRideResponse.getPrice().getDisplay());
			System.out.println("\tTrip DistanceEstimate: " + estimateRideResponse.getTrip().getDistanceEstimate());
			System.out.println("\tTrip DistanceUnit: " + estimateRideResponse.getTrip().getDistanceUnit());
			System.out.println("\tDurationEstimate: " + estimateRideResponse.getTrip().getDurationEstimate());
		}
		System.out.println("}");
	}

	/**
	 * For now the first product is picked. Null is returned when there are no
	 * products.
	 */
	private static Product pickProduct(final List<Product> products) {
		for (final Product product : products) {
			return product;
		}

		return null;
	}

	private static class Location {
		private final float latitude;
		private final float longitude;

		private Location(final float latitude, final float longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		private float getLatitude() {
			return latitude;
		}

		private float getLongitude() {
			return longitude;
		}
	}

	private static class AppProperties {
		private final String clientId;
		private final String clientSecret;
		private final String serverToken;
		private final String user;

		private AppProperties(final InputStream inputStream) {
			final Properties appProperties = new Properties();
			try {
				appProperties.load(inputStream);
			} catch (final IOException e) {
				throw new IllegalStateException("Something went wrong when reading from app.properties.");
			}

			clientId = appProperties.getProperty("CLIENT_ID");
			clientSecret = appProperties.getProperty("CLIENT_SECRET");
			serverToken = appProperties.getProperty("SERVER_TOKEN");
			user = appProperties.getProperty("USER");
		}

		public String getClientId() {
			return clientId;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public String getServerToken() {
			return serverToken;
		}

		public String getUser() {
			return user;
		}
	}
}
