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
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.Session.Environment;
import com.uber.sdk.rides.client.UberRidesServices;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.error.ApiException;
import com.uber.sdk.rides.client.error.NetworkException;
import com.uber.sdk.rides.client.model.PaymentMethod;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideReceipt;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.RideRequestParameters.Builder;
import com.uber.sdk.rides.client.model.SandboxRideRequestParameters;
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

			final Location pickupLocation = TNW;
			final Location dropoffLocation = HOME;

			final OAuth2Credentials credentials = getCredentials();

			final String user = getUserNameFromUser();

			System.out.println("Execute this URL in your browser and copy the value of code: "
					+ credentials.getAuthorizationUrl());

			final String authorizationCode = getAuthorizationCodeFromUser();

			final UberRidesSyncService service = getService(credentials, user, authorizationCode);

			final PaymentMethod paymentMethod = getPaymentMethod(service);

			final Product product = getProduct(service, TNW);

			showProduct(service, product, pickupLocation, dropoffLocation, paymentMethod);

			final Ride ride = getRide(service, pickupLocation, dropoffLocation, product, paymentMethod);

			showRideInfo(service);

			updateSandboxRide(service, RideStatus.ACCEPTED);

			showRideInfo(service);

			updateSandboxRide(service, RideStatus.ARRIVING);

			showRideInfo(service);

			updateSandboxRide(service, RideStatus.IN_PROGRESS);

			showRideInfo(service);

			updateSandboxRide(service, RideStatus.COMPLETED);

			showRideReceipt(service, ride.getRideId());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void showRideReceipt(final UberRidesSyncService service, final String rideId)
			throws ApiException, NetworkException {
		final RideReceipt rideReceipt = service.getRideReceipt(rideId).getBody();
		System.out.println("Ride Receipt {");
		System.out.println("\tcurrency code: " + rideReceipt.getCurrencyCode());
		System.out.println("\tdistance: " + rideReceipt.getDistance());
		System.out.println("\tdistance label: " + rideReceipt.getDistanceLabel());
		System.out.println("\tduration: " + rideReceipt.getDuration());
		System.out.println("\tnormal fare: " + rideReceipt.getNormalFare());
		System.out.println("\tride id: " + rideReceipt.getRideId());
		System.out.println("\tsubtotal" + rideReceipt.getSubTotal());
		System.out.println("\ttotal charged" + rideReceipt.getTotalCharged());
		System.out.println("}");
	}

	private void showRideInfo(final UberRidesSyncService service) throws ApiException, NetworkException {
		final Ride ride = service.getCurrentRide().getBody();
		System.out.println("Ride {");
		System.out.println("\trideId: " + ride.getRideId());
		System.out.println("\tstatus: " + ride.getStatus());
		System.out.println("\tETA (in minutes): " + ride.getEta());
		System.out.println("\tsurge muliplier: " + ride.getSurgeMultiplier());
		if (ride.getLocation() != null) {
			System.out.println("\tlatitude: " + ride.getLocation().getLatitude());
			System.out.println("\tlongitude: " + ride.getLocation().getLongitude());
			System.out.println("\tbearing: " + ride.getLocation().getBearing());
		}
		if (ride.getVehicle() != null) {
			System.out.println("\tvehicle license plate: " + ride.getVehicle().getLicensePlate());
			System.out.println("\tvehicle make: " + ride.getVehicle().getMake());
			System.out.println("\tvehicle model: " + ride.getVehicle().getModel());
			System.out.println("\tvehicle picture (URL): " + ride.getVehicle().getPictureUrl());
		}
		if (ride.getDriver() != null) {
			System.out.println("\tdriver name: " + ride.getDriver().getName());
			System.out.println("\tdriver phone number: " + ride.getDriver().getPhoneNumber());
			System.out.println("\tdriver rating: " + ride.getDriver().getRating());
			System.out.println("\tdriver picture (URL): " + ride.getDriver().getPictureUrl());
		}
		System.out.println("}");
	}

	/**
	 * Returns the 'paymentless' payment method.
	 */
	private PaymentMethod getPaymentMethod(final UberRidesSyncService service) {
		return new PaymentMethod() {
			public String getDesription() {
				return getPaymentMethodId();
			}

			public String getPaymentMethodId() {
				return "paymentless";
			}

			public String getType() {
				return getPaymentMethodId();
			}
		};
	}

	private Ride getRide(final UberRidesSyncService service,
			final Location pickupLocation,
			final Location dropoffLocation,
			final Product product,
			final PaymentMethod paymentMethod) throws ApiException, NetworkException {
		return service.requestRide(getRideRequestParametersBuilder(pickupLocation, dropoffLocation, paymentMethod)
				.setProductId(product.getProductId()).build()).getBody();
	}

	private Product getProduct(final UberRidesSyncService service, final Location pickupLocation)
			throws ApiException, NetworkException {
		final List<Product> products = service.getProducts(pickupLocation.getLatitude(), pickupLocation.getLongitude())
				.getBody().getProducts();

		return pickProduct(products);
	}

	private Builder getRideRequestParametersBuilder(final Location pickupLocation,
			final Location dropoffLocation,
			final PaymentMethod paymentMethod) {
		return new RideRequestParameters.Builder()
				.setPickupCoordinates(pickupLocation.getLatitude(), pickupLocation.getLongitude())
				.setDropoffCoordinates(dropoffLocation.getLatitude(), dropoffLocation.getLongitude())
				.setPaymentMethodId(paymentMethod.getPaymentMethodId());
	}

	private UberRidesSyncService getService(final OAuth2Credentials credentials,
			final String user,
			final String authorizationCode) {
		final Credential credential = credentials.authenticate(authorizationCode, user);

		final Session session = new Session.Builder().setCredential(credential).setEnvironment(Environment.SANDBOX)
				.build();

		return UberRidesServices.createSync(session);
	}

	private OAuth2Credentials getCredentials() {
		final Collection<Scope> scopes = new ArrayList<Scope>();
		scopes.add(Scope.PROFILE);
		scopes.add(Scope.REQUEST);
		scopes.add(Scope.ALL_TRIPS);
		scopes.add(Scope.HISTORY);
		scopes.add(Scope.PLACES);
		scopes.add(Scope.REQUEST_RECEIPT);
		return new OAuth2Credentials.Builder()
				.setClientSecrets(appProperties.getClientId(), appProperties.getClientSecret())
				.setRedirectUri(OAUTH2_CALLBACK_URL).setScopes(scopes).build();
	}

	private static String getUserNameFromUser() throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the name of the Uber user: ");
		return br.readLine();
	}

	private static String getAuthorizationCodeFromUser() throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Paste the value of name 'code' here: ");
		return br.readLine();
	}

	private static void updateSandboxRide(final UberRidesSyncService service, final RideStatus status)
			throws ApiException, NetworkException {
		try {
			final Ride ride = service.getCurrentRide().getBody();
			service.updateSandboxRide(ride.getRideId(),
					new SandboxRideRequestParameters.Builder().setStatus(status.getStatus()).build());
		} catch (final Exception e) {
		}
	}

	private void showProduct(final UberRidesSyncService service,
			final Product product,
			final Location pickupLocation,
			final Location dropoffLocation,
			final PaymentMethod paymentMethod) throws ApiException, NetworkException {

		final Builder rideRequestParametersBuilder = getRideRequestParametersBuilder(pickupLocation, dropoffLocation,
				paymentMethod);
		final TimeEstimatesResponse etaResponse = service.getPickupTimeEstimates(pickupLocation.getLatitude(),
				pickupLocation.getLongitude(), product.getProductId()).getBody();
		System.out.println("Product {");
		System.out.println("\tDisplayName: " + product.getDisplayName());
		System.out.println("\tDescription: " + product.getDescription());
		System.out.println("\tProductId: " + product.getProductId());
		System.out.println("\tCapacity: " + product.getCapacity());
		System.out.println("\tImage: " + product.getImage());
		for (final TimeEstimate eta : etaResponse.getTimes()) {
			System.out.println("\tETA in seconds: " + eta.getEstimate());
		}
		final RideEstimate estimateRideResponse = service
				.estimateRide(rideRequestParametersBuilder.setProductId(product.getProductId()).build()).getBody();
		System.out.println("\tPickupEstimate in minutes: " + estimateRideResponse.getPickupEstimate());
		System.out.println("\tPrice (Display): " + estimateRideResponse.getPrice().getDisplay());
		System.out.println("\tTrip DistanceEstimate: " + estimateRideResponse.getTrip().getDistanceEstimate());
		System.out.println("\tTrip DistanceUnit: " + estimateRideResponse.getTrip().getDistanceUnit());
		System.out.println("\tDurationEstimate: " + estimateRideResponse.getTrip().getDurationEstimate());
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

	private enum RideStatus {

		PROCESSING("processing", "The Request is matching to the most efficient available driver."),

		NO_DRIVERS_AVAILABLE("no_drivers_available", "The Request was unfulfilled because no drivers were available."),

		ACCEPTED("accepted",
				"The Request has been accepted by a driver and is \"en route\" to the start location (i.e. start_latitude and start_longitude)."),

		ARRIVING("arriving", "The driver has arrived or will be shortly."),

		IN_PROGRESS("in_progress", "The Request is \"en route\" from the start location to the end location."),

		DRIVER_CANCELED("driver_canceled", "The Request has been canceled by the driver."),

		RIDER_CANCELED("rider_canceled", "The Request canceled by rider."),

		COMPLETED("completed", "Request has been completed by the driver.");

		private final String status;
		private final String description;

		private RideStatus(final String status, final String description) {
			this.status = status;
			this.description = description;
		}

		public String getStatus() {
			return status;
		}

		public String getDescription() {
			return description;
		}
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
	}
}
