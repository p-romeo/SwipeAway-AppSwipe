package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [35])
  fun greeting_screenshot_pixel8() {
    composeTestRule.setContent { 
      MyApplicationTheme { 
        androidx.compose.material3.Text("AppSwipe Simulator") 
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting_pixel8.png")
  }

  @Test
  @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet, sdk = [35])
  fun greeting_screenshot_tablet() {
    composeTestRule.setContent { 
      MyApplicationTheme { 
        androidx.compose.material3.Text("AppSwipe Simulator") 
      } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting_tablet.png")
  }
}
