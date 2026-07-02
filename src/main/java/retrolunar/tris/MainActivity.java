package retrolunar.tris;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private CameraManager camera;
    private String cameraId;
    private boolean flashlight;
    private ImageView flashlightImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemBars();
        camera = getSystemService(CameraManager.class);
        cameraId = findFlash();
        flashlightImage = findViewById(R.id.flashlightImage);
        flashlightImage.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float dx = event.getX() - startX;
                        float dy = event.getY() - startY;
                        if (Math.sqrt(dx * dx + dy * dy) < 20) {
                            toggle();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemBars();
        }
    }

    @SuppressWarnings("deprecation")
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    private String findFlash() {
        try {
            for (String id : camera.getCameraIdList()) {
                CameraCharacteristics c = camera.getCameraCharacteristics(id);
                Boolean available = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (available != null && available) {
                    return id;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void torch(boolean on) {
        try {
            if (cameraId != null) {
                camera.setTorchMode(cameraId, on);
            }
        } catch (Exception ignored) {
        }
    }

    private void toggle() {
        if (cameraId == null) return;
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }
        flashlight = !flashlight;
        torch(flashlight);
        flashlightImage.setImageResource(flashlight ? R.drawable.flashlight_on : R.drawable.flashlight_off);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(code, permissions, results);
        if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
            toggle();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (flashlight) {
            torch(false);
            flashlight = false;
            flashlightImage.setImageResource(R.drawable.flashlight_off);
        }
    }
}
