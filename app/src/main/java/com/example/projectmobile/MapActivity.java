package com.example.projectmobile;

import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.internal.ui.LocationComponent;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;
import com.mapbox.turf.TurfMeasurement;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class MapActivity extends AppCompatActivity implements SensorEventListener {
    MapView mapview;
    MaterialButton setRoute;
    FloatingActionButton fab, fabadd;
    Point point;
    DatabaseReference reference = null;
    SharePothole location;
    private PointAnnotationManager pointAnnotationManager1;
    private PointAnnotationManager pointAnnotationManager;

    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;
    private MapboxRouteLineApi routeLineApi;

    private String permissionGranted;
    private final Set<String> warnedPotholes = new HashSet<>();


    //Sensor Accelerometer
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Button confirmButton;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private static final float BUMP_THRESHOLD = 5.0f;
    private static final float STATIC_THRESHOLD = 0.5f;
    private float lastAcceleration = 0;
    private float maxAccelerationInSession = 0;
    private boolean isBumpSessionActive = false;
    private boolean isDeviceStatic = true; // Cờ trạng thái tĩnh của thiết bị
    private long lastStaticCheckTime = 0;
    private long bumpSessionStartTime = 0;
    private static final long SESSION_DURATION = 2000; // 2 giây để kết thúc một phiên phát hiện ổ gà
    private static final long STATIC_CHECK_DURATION = 500; // 500ms để kiểm tra trạng thái tĩnh
    private boolean isFirstReading = true;

    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {
            // Cập nhật vị trí mới nếu cần
        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);

            // Lấy vị trí hiện tại của người dùng
            double userLatitude = location.getLatitude();
            double userLongitude = location.getLongitude();

            // Kiểm tra khoảng cách với các ổ gà
            checkProximityToPotholes(userLatitude, userLongitude);

            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
                point = Point.fromLngLat(location.getLongitude(), location.getLatitude());
            }
        }
    };

    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    mapview.getMapboxMap().getStyle(style -> {
                        if (style != null) {
                            routeLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                        }
                    });
                }
            });
        }
    };

    boolean focusLocation = true;
    private MapboxNavigation mapboxNavigation;
    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(16.5)
                .padding(new EdgeInsets(0.0, 0.0, 0.0, 0.0)).bearing(bearing).build();

        getCamera(mapview).easeTo(cameraOptions, animationOptions);
    }


    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                // Quyền đã được cấp, kích hoạt các tính năng liên quan
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissionGranted)) {
                    startLocationUpdates();
                } else if (Manifest.permission.POST_NOTIFICATIONS.equals(permissionGranted)) {
                    enableNotifications();
                }
                recreate();
            } else {

            }
        }
    });

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapboxNavigation.startTripSession();
            fab.hide();
            LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapview);
            locationComponentPlugin.setEnabled(true);
            locationComponentPlugin.setLocationProvider(navigationLocationProvider);
        }
    }

    private void enableNotifications() {
        // Đây là nơi bạn cấu hình cho thông báo nếu cần
        Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            startLocationUpdates(); // Nếu quyền đã cấp, kích hoạt ngay lập tức
        }
    }

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocation = false;
            getGestures(mapview).removeOnMoveListener(this);
            fab.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
        }
    };

    private void checkProximityToPotholes(double userLatitude, double userLongitude) {
        FirebaseDatabase.getInstance().getReference().child("sharedPothole").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SharePothole pothole = dataSnapshot.getValue(SharePothole.class);
                    if (pothole != null) {
                        double potholeLatitude = pothole.getLatitude();
                        double potholeLongitude = pothole.getLongitude();

                        // Tính khoảng cách
                        float distance = calculateDistance(userLatitude, userLongitude, potholeLatitude, potholeLongitude);

                        // Kiểm tra cảnh báo
                        if (!warnedPotholes.contains(pothole.getId()) && distance < 50) {
                            triggerPotholeAlert(pothole);
                            warnedPotholes.add(pothole.getId()); // Đánh dấu ổ gà đã được cảnh báo
                        } else if (warnedPotholes.contains(pothole.getId()) && distance > 60) {
                            warnedPotholes.remove(pothole.getId()); // Reset trạng thái nếu người dùng rời xa ổ gà
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu ổ gà", error.toException());
            }
        });
    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính trái đất (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (float) (R * c * 1000); // Trả về khoảng cách theo mét
    }

    private void triggerPotholeAlert(SharePothole pothole) {
        // Rung điện thoại
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(2000, 255));
        }

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        mediaPlayer.start();

        // Hiển thị thông báo
        Toast.makeText(this, "Cảnh báo: Sắp đến ổ gà ", Toast.LENGTH_LONG).show();

        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.release();
        });
    }

    //Search
    private PlaceAutocomplete placeAutocomplete;
    private SearchResultsView searchResultsView;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private TextInputEditText searchET;
    private Boolean ignoreNextQueryUpdate = false;

    private Handler handler = new Handler();
    private Runnable searchRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        //Sensor Accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Search
        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchET = findViewById(R.id.searchET);
        searchResultsView = findViewById(R.id.searchResultsView);
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(MapActivity.this));

        MapboxRouteLineOptions options = new MapboxRouteLineOptions.Builder(this).withRouteLineResources(new RouteLineResources.Builder().build())
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER).build();
        routeLineView = new MapboxRouteLineView(options);
        routeLineApi = new MapboxRouteLineApi(options);

        NavigationOptions navigationOptions = new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build();

        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);

        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);

        //Maker Pothole
        FirebaseApp.initializeApp(this);
        location = new SharePothole();

        mapview = findViewById(R.id.mapView);
        fabadd = findViewById(R.id.fabadd);
        fab = findViewById(R.id.fab);
        fab.hide();
        setRoute = findViewById(R.id.setRoute);

        checkPermissions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }

        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapview);
        getGestures(mapview).addOnMoveListener(onMoveListener);

        mapview.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapview.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(16.5).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapview).addOnMoveListener(onMoveListener);

                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.warning);
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.location);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapview);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, new AnnotationConfig());
                pointAnnotationManager1 = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, new AnnotationConfig());

                addOnMapClickListener(mapview.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        pointAnnotationManager1.deleteAll();
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap1)
                                .withPoint(point);
                        pointAnnotationManager1.create(pointAnnotationOptions);

                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRoute(point);
                            }
                        });
                        return true;
                    }
                });

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        getGestures(mapview).addOnMoveListener(onMoveListener);
                        fab.hide();
                    }
                });

                FirebaseDatabase.getInstance().getReference().child("sharedPothole").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pointAnnotationManager.deleteAll();
                        snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                            @Override
                            public void accept(DataSnapshot dataSnapshot) {
                                SharePothole location1 = dataSnapshot.getValue(SharePothole.class);
                                if (location1 != null && !location1.getId().equals(MapActivity.this.location.getId())) {
                                    if (bitmap != null) {  // Kiểm tra xem bitmap có null không
                                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                                .withTextAnchor(TextAnchor.CENTER)
                                                .withIconImage(bitmap)
                                                .withPoint(Point.fromLngLat(location1.getLongitude(), location1.getLatitude()));

                                        pointAnnotationManager.create(pointAnnotationOptions);
                                    } else {
                                        Toast.makeText(MapActivity.this, "Error bitmap", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
                            @Override
                            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                                snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                                    @Override
                                    public void accept(DataSnapshot dataSnapshot) {
                                        SharePothole location1 = dataSnapshot.getValue(SharePothole.class);
                                        // Kiểm tra vị trí của marker và dữ liệu trong Firebase có trùng khớp không
                                        if (location1 != null && pointAnnotation.getPoint().longitude() == location1.getLongitude() && pointAnnotation.getPoint().latitude() == location1.getLatitude()) {
                                            String info = "Tên người đóng góp: " + location1.getName() + "\n" +
                                                    "Vị trí: " + location1.getLatitude() + ", " + location1.getLongitude() + "\n" +
                                                    "Ngày đóng góp: " + location1.getDate() + "\n" +
                                                    "Mức độ: " + location1.getSeverity();

                                            // Tạo Dialog để hiển thị thông tin
                                            new AlertDialog.Builder(MapActivity.this)
                                                    .setTitle("Thông tin ổ gà")
                                                    .setMessage(info)  // Hiển thị thông tin ổ gà
                                                    .setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();  // Đóng Dialog khi nhấn "Đóng"
                                                        }
                                                    })
                                                    .setCancelable(true)  // Cho phép đóng Dialog khi nhấn ngoài vùng dialog
                                                    .show();
                                        }
                                    }
                                });
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

                fabadd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            return;
                        }

                        String name = user.getDisplayName();
                        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                        String[] severityOptions = {"Nhẹ", "Vừa", "Nặng"};
                        final String[] selectedSeverity = new String[1];  // Sử dụng mảng để giữ giá trị

                        // Tạo dialog để người dùng chọn mức độ
                        new AlertDialog.Builder(MapActivity.this)
                                .setTitle("Chọn mức độ ổ gà")
                                .setSingleChoiceItems(severityOptions, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Lưu mức độ người dùng chọn vào selectedSeverity
                                        selectedSeverity[0] = severityOptions[which];
                                    }
                                })
                                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Chia sẻ vị trí và thông tin ổ gà
                                        Toast.makeText(MapActivity.this, "Đang chia sẻ ổ gà...", Toast.LENGTH_SHORT).show();

                                        // Tạo reference mới mỗi lần nhấn nút
                                        DatabaseReference newReference = FirebaseDatabase.getInstance().getReference().child("sharedPothole").push();

                                        // Tạo thông tin ổ gà mới
                                        SharePothole location = new SharePothole();
                                        location.setId(newReference.getKey());  // Lấy key của reference mới tạo
                                        location.setName(name);
                                        location.setLongitude(point.longitude());
                                        location.setLatitude(point.latitude());
                                        location.setDate(currentDate);
                                        location.setSeverity(selectedSeverity[0]);  // Sử dụng giá trị từ selectedSeverity

                                        // Lưu ổ gà vào Firebase
                                        newReference.setValue(location);
                                    }
                                })
                                .setNegativeButton("Hủy", null)  // Nếu người dùng không muốn chọn mức độ, có thể hủy
                                .show();

                    }
                });

                placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
                    @Override
                    public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

                    }

                    @Override
                    public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        ignoreNextQueryUpdate = true;
                        focusLocation = false;
                        searchET.setText(placeAutocompleteSuggestion.getName());
                        searchResultsView.setVisibility(View.GONE);
                        pointAnnotationManager1.deleteAll();
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap1)
                                .withPoint(placeAutocompleteSuggestion.getCoordinate());
                        pointAnnotationManager1.create(pointAnnotationOptions);
                        updateCamera(placeAutocompleteSuggestion.getCoordinate(), 0.0);

                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchRoute(placeAutocompleteSuggestion.getCoordinate());
                            }
                        });
                    }

                    @Override
                    public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        //queryEditText.setText(placeAutocompleteSuggestion.getName());
                    }

                    @Override
                    public void onError(@NonNull Exception e) {

                    }
                });
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Không làm gì
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Nếu flag ignoreNextQueryUpdate đang bật, bỏ qua
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    // Hủy bỏ yêu cầu trước đó nếu người dùng tiếp tục gõ
                    handler.removeCallbacks(searchRunnable);

                    // Tạo một yêu cầu tìm kiếm mới sau 500ms khi người dùng ngừng nhập
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String query = charSequence.toString();
                            // Thực hiện tìm kiếm sau khi người dùng ngừng gõ
                            placeAutocompleteUiAdapter.search(query, new Continuation<Unit>() {
                                @NonNull
                                @Override
                                public CoroutineContext getContext() {
                                    return EmptyCoroutineContext.INSTANCE;
                                }

                                @Override
                                public void resumeWith(@NonNull Object o) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchResultsView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            });
                        }
                    };

                    // Đặt lại delay 1000ms để gửi yêu cầu
                    handler.postDelayed(searchRunnable, 1000);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Không làm gì
            }
        });

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapActivity.this, "Please select a location in map", Toast.LENGTH_SHORT).show();
            }
        });

        //BottomNavigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        // Gắn sự kiện khi click vào các item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.dashboard) {
                    Intent intent = new Intent(MapActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.user) {
                    Intent intent = new Intent(MapActivity.this, UpdateProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.setting) {
                    Intent intent = new Intent(MapActivity.this, SettingActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void fetchRoute(Point destination) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(MapActivity.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                setRoute.setEnabled(false);
                setRoute.setText("Fetching route...");

                RouteOptions.Builder builder = RouteOptions.builder()
                        .language("vi")
                        .steps(true)
                        .profile(DirectionsCriteria.PROFILE_DRIVING);

                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, destination));
                builder.alternatives(false);
                builder.profile(DirectionsCriteria.PROFILE_DRIVING);
                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        fab.performClick();
                        mapboxNavigation.setNavigationRoutes(list);
                        setRoute.setEnabled(true);
                        setRoute.setText("Set route");

                        mapboxNavigation.registerLocationObserver(new LocationObserver() {
                            @Override
                            public void onNewRawLocation(@NonNull Location rawLocation) {
                            }

                            @Override
                            public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
                                Location currentLocation = locationMatcherResult.getEnhancedLocation();
                                if (!isUserOnRoute(currentLocation, list.get(0))) {
                                    // If user is off route, fetch a new route
                                    fetchRoute(destination);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        setRoute.setEnabled(true);
                        setRoute.setText("Set route");
                        Toast.makeText(MapActivity.this, "Route request failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MapActivity.this, "Location request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isUserOnRoute(Location currentLocation, NavigationRoute route) {
        // Define acceptable deviation threshold in meters
        double deviationThreshold = 50.0;

        // Calculate distance from user's location to nearest point on the route
        Point currentPoint = Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude());
        double distanceFromRoute = TurfMeasurement.distance(currentPoint, route.getRouteOptions().coordinatesList().get(0));

        // If user is more than deviation threshold away from the route, return false
        return distanceFromRoute < deviationThreshold;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapboxNavigation.onDestroy();
        mapboxNavigation.unregisterRoutesObserver(routesObserver);
        mapboxNavigation.unregisterLocationObserver(locationObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (isFirstReading) {
                isFirstReading = false; // Đã qua lần đọc đầu tiên
                lastAcceleration = (float) Math.sqrt(
                        event.values[0] * event.values[0] +
                                event.values[1] * event.values[1] +
                                event.values[2] * event.values[2]
                ); // Khởi tạo giá trị gia tốc
                return; // Bỏ qua xử lý lần đầu tiên
            }

            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            float acceleration = (float) Math.sqrt(
                    linear_acceleration[0] * linear_acceleration[0] +
                            linear_acceleration[1] * linear_acceleration[1] +
                            linear_acceleration[2] * linear_acceleration[2]
            );

            // Kiểm tra trạng thái tĩnh
            long currentTime = System.currentTimeMillis();
            if (acceleration < STATIC_THRESHOLD) {
                // Nếu gia tốc nhỏ hơn ngưỡng STATIC_THRESHOLD, thiết bị có thể đang tĩnh
                if (currentTime - lastStaticCheckTime > STATIC_CHECK_DURATION) {
                    isDeviceStatic = true;
                }
            } else {
                // Nếu gia tốc vượt quá STATIC_THRESHOLD, thiết bị không còn tĩnh
                isDeviceStatic = false;
                lastStaticCheckTime = currentTime;
            }

            if (!isBumpSessionActive && !isDeviceStatic) {
                // Nếu không có phiên và thiết bị không tĩnh, kiểm tra dao động đột ngột
                float deltaAcceleration = Math.abs(acceleration - lastAcceleration);
                if (deltaAcceleration > BUMP_THRESHOLD) {
                    // Bắt đầu phiên phát hiện ổ gà
                    isBumpSessionActive = true;
                    bumpSessionStartTime = currentTime;
                    maxAccelerationInSession = acceleration;
                    Toast.makeText(MapActivity.this, "Đang phát hiện ổ gà...", Toast.LENGTH_SHORT).show();
                }
            } else if (isBumpSessionActive) {
                // Nếu có phiên, cập nhật gia tốc lớn nhất
                maxAccelerationInSession = Math.max(maxAccelerationInSession, acceleration);

                // Kiểm tra xem phiên đã kết thúc chưa
                if (currentTime - bumpSessionStartTime > SESSION_DURATION) {
                    isBumpSessionActive = false;
                    String severity = classifyBump(maxAccelerationInSession);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        return;
                    }

                    String name = user.getDisplayName();
                    String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                    new AlertDialog.Builder(MapActivity.this)
                            .setTitle("Chọn mức độ ổ gà")
                            .setMessage("Phát hiện 1 ổ gà với mức độ: " + severity + ". Bạn có muốn lưu thông tin này không?")
                            .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Chia sẻ vị trí và thông tin ổ gà
                                    Toast.makeText(MapActivity.this, "Đang chia sẻ ổ gà...", Toast.LENGTH_SHORT).show();

                                    // Tạo reference mới mỗi lần nhấn nút
                                    DatabaseReference newReference = FirebaseDatabase.getInstance().getReference().child("sharedPothole").push();

                                    // Tạo thông tin ổ gà mới
                                    SharePothole location = new SharePothole();
                                    location.setId(newReference.getKey());  // Lấy key của reference mới tạo
                                    location.setName(name);
                                    location.setLongitude(point.longitude());
                                    location.setLatitude(point.latitude());
                                    location.setDate(currentDate);
                                    location.setSeverity(severity);  // Sử dụng giá trị từ selectedSeverity

                                    // Lưu ổ gà vào Firebase
                                    newReference.setValue(location);
                                }
                            })
                            .setNegativeButton("Hủy", null)  // Nếu người dùng không muốn chọn mức độ, có thể hủy
                            .show();
                }
            }
            lastAcceleration = acceleration;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý
    }

    private String classifyBump(float acceleration) {
        if (acceleration < 20) {
            return "Nhẹ";
        } else if (acceleration < 40) {
            return "Vừa";
        } else {
            return "Nặng";
        }
    }
}