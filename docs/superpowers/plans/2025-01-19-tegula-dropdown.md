# Tegula Dropdown Menu Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a floating-card-style animated dropdown menu component as a new `:dropdowns` module.

**Architecture:** New `:dropdowns` module with XML layout, drawables, and animations. Animation uses `android:animateLayoutChanges="true"` + alpha animation resources. Optional `TegulaDropdown` Java helper in the same module for convenience toggle/select methods.

**Tech Stack:** Android XML resources, Android SDK 21+, pure platform APIs (no androidx)

---

### Task 1: Create `:dropdowns` module scaffolding

**Files:**
- Create: `dropdowns/build.gradle`
- Modify: `settings.gradle` (add `include ':dropdowns'`)

- [ ] **Step 1: Create dropdowns/build.gradle**

```groovy
plugins {
    alias(libs.plugins.android.library)
}

def rootName = rootProject.name
def colorsPath = (rootName == 'Tegula') ? ':colors' : ':Tegula:colors'

android {
    namespace 'com.venddair.tegula.dropdowns'
    compileSdk 36

    defaultConfig {
        minSdk 21
    }
}

dependencies {
    api project(colorsPath)
}
```

- [ ] **Step 2: Add `:dropdowns` to settings.gradle**

Append this line to `settings.gradle` after `include ':core'`:

```groovy
include ':dropdowns'
```

- [ ] **Step 3: Create directory structure**

```bash
mkdir -p dropdowns/src/main/res/{layout,drawable,anim,values}
mkdir -p dropdowns/src/main/java/com/venddair/tegula/dropdowns
```

- [ ] **Step 4: Verify module syncs**

Run: `./gradlew :dropdowns:tasks --quiet 2>&1 | head -5`
Expected: No errors, tasks list appears

- [ ] **Step 5: Commit**

```bash
git add dropdowns/build.gradle settings.gradle
git commit -m "feat: add :dropdowns module scaffolding"
```

---

### Task 2: Create dropdown drawables

**Files:**
- Create: `dropdowns/src/main/res/drawable/tegula_dropdown_bg.xml`
- Create: `dropdowns/src/main/res/drawable/tegula_dropdown_item_bg.xml`
- Create: `dropdowns/src/main/res/drawable/tegula_dropdown_chevron.xml`

- [ ] **Step 1: Create `tegula_dropdown_bg.xml` — rounded card background**

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="12dp" />
    <solid android:color="#FFFFFF" />
</shape>
```

- [ ] **Step 2: Create `tegula_dropdown_item_bg.xml` — item selector**

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <corners android:radius="8dp" />
            <solid android:color="@color/tegula_bg_card" />
        </shape>
    </item>
    <item android:state_activated="true">
        <shape android:shape="rectangle">
            <corners android:radius="8dp" />
            <solid android:color="@color/tegula_accent" />
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <corners android:radius="8dp" />
            <solid android:color="@android:color/transparent" />
        </shape>
    </item>
</selector>
```

- [ ] **Step 3: Create `tegula_dropdown_chevron.xml` — chevron arrow**

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="12dp"
    android:height="8dp"
    android:viewportWidth="12"
    android:viewportHeight="8">
    <path
        android:pathData="M1,1 L6,6 L11,1"
        android:strokeColor="@color/tegula_text_secondary"
        android:strokeWidth="2"
        android:fillColor="@android:color/transparent"
        android:strokeLineCap="round" />
</vector>
```

- [ ] **Step 4: Verify drawable compilation**

Run: `./gradlew :dropdowns:mergeDebugResources --quiet 2>&1 | tail -3`
Expected: BUILD SUCCESSFUL, no errors

- [ ] **Step 5: Commit**

```bash
git add dropdowns/src/main/res/drawable/
git commit -m "feat: add dropdown drawables (bg, item selector, chevron)"
```

---

### Task 3: Create animation resources

**Files:**
- Create: `dropdowns/src/main/res/anim/tegula_dropdown_fade_in.xml`
- Create: `dropdowns/src/main/res/anim/tegula_dropdown_fade_out.xml`

- [ ] **Step 1: Create `tegula_dropdown_fade_in.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="200"
    android:fromAlpha="0.0"
    android:toAlpha="1.0" />
```

- [ ] **Step 2: Create `tegula_dropdown_fade_out.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="200"
    android:fromAlpha="1.0"
    android:toAlpha="0.0" />
```

- [ ] **Step 3: Verify animation resources compile**

Run: `./gradlew :dropdowns:mergeDebugResources --quiet 2>&1 | tail -3`
Expected: BUILD SUCCESSFUL, no errors

- [ ] **Step 4: Commit**

```bash
git add dropdowns/src/main/res/anim/
git commit -m "feat: add dropdown fade animations"
```

---

### Task 4: Create dropdown layout

**Files:**
- Create: `dropdowns/src/main/res/layout/tegula_dropdown.xml`

- [ ] **Step 1: Create `tegula_dropdown.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <!-- Trigger -->
    <LinearLayout
        android:id="@+id/tegula_dropdown_trigger"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@drawable/tegula_dropdown_bg"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:paddingStart="16dp"
        android:paddingEnd="16dp"
        android:clickable="true"
        android:focusable="true"
        android:elevation="2dp">

        <TextView
            android:id="@+id/tegula_dropdown_label"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="@string/tegula_dropdown_select"
            android:textColor="@color/tegula_text_primary"
            android:textSize="14sp" />

        <ImageView
            android:id="@+id/tegula_dropdown_chevron"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/tegula_dropdown_chevron"
            android:contentDescription="@null" />
    </LinearLayout>

    <!-- Dropdown list (starts hidden, animated) -->
    <LinearLayout
        android:id="@+id/tegula_dropdown_list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:layout_marginTop="4dp"
        android:background="@drawable/tegula_dropdown_bg"
        android:elevation="4dp"
        android:padding="4dp"
        android:animateLayoutChanges="true"
        android:visibility="gone">

        <TextView
            android:id="@+id/tegula_dropdown_item_1"
            android:layout_width="match_parent"
            android:layout_height="48dp"
            android:background="@drawable/tegula_dropdown_item_bg"
            android:gravity="center_vertical"
            android:paddingStart="16dp"
            android:paddingEnd="16dp"
            android:textColor="@color/tegula_text_primary"
            android:textSize="14sp"
            android:clickable="true"
            android:focusable="true" />

        <TextView
            android:id="@+id/tegula_dropdown_item_2"
            android:layout_width="match_parent"
            android:layout_height="48dp"
            android:background="@drawable/tegula_dropdown_item_bg"
            android:gravity="center_vertical"
            android:paddingStart="16dp"
            android:paddingEnd="16dp"
            android:textColor="@color/tegula_text_primary"
            android:textSize="14sp"
            android:clickable="true"
            android:focusable="true" />

        <TextView
            android:id="@+id/tegula_dropdown_item_3"
            android:layout_width="match_parent"
            android:layout_height="48dp"
            android:background="@drawable/tegula_dropdown_item_bg"
            android:gravity="center_vertical"
            android:paddingStart="16dp"
            android:paddingEnd="16dp"
            android:textColor="@color/tegula_text_primary"
            android:textSize="14sp"
            android:clickable="true"
            android:focusable="true" />
    </LinearLayout>
</LinearLayout>
```

- [ ] **Step 2: Create `dropdowns/src/main/res/values/strings.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="tegula_dropdown_select">Select option</string>
</resources>
```

- [ ] **Step 3: Verify layout compilation**

Run: `./gradlew :dropdowns:mergeDebugResources --quiet 2>&1 | tail -3`
Expected: BUILD SUCCESSFUL, no errors

- [ ] **Step 4: Commit**

```bash
git add dropdowns/src/main/res/layout/ dropdowns/src/main/res/values/strings.xml
git commit -m "feat: add dropdown layout with animated list container"
```

---

### Task 5: Create TegulaDropdown helper class

**Files:**
- Create: `dropdowns/src/main/java/com/venddair/tegula/dropdowns/TegulaDropdown.java`
- Create: `dropdowns/src/main/res/values/styles.xml`

- [ ] **Step 1: Create `TegulaDropdown.java`**

```java
package com.venddair.tegula.dropdowns;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Convenience helper for Tegula dropdown menus.
 * All methods are optional — you can toggle visibility directly.
 */
public class TegulaDropdown {

    /**
     * Toggles the dropdown list open/closed with animation.
     * Call this on the root dropdown view (the <include> target).
     */
    public static void toggle(View dropdownRoot) {
        View list = dropdownRoot.findViewById(R.id.tegula_dropdown_list);
        ImageView chevron = dropdownRoot.findViewById(R.id.tegula_dropdown_chevron);
        if (list == null) return;

        if (list.getVisibility() == View.GONE) {
            open(dropdownRoot, list, chevron);
        } else {
            close(dropdownRoot, list, chevron);
        }
    }

    /**
     * Opens the dropdown with fade-in animation.
     */
    public static void open(View dropdownRoot, View list, ImageView chevron) {
        list.setVisibility(View.VISIBLE);
        Animation fade = AnimationUtils.loadAnimation(
                dropdownRoot.getContext(), R.anim.tegula_dropdown_fade_in);
        list.startAnimation(fade);
        if (chevron != null) chevron.setRotation(180);
    }

    /**
     * Closes the dropdown with fade-out animation.
     */
    public static void close(View dropdownRoot, View list, ImageView chevron) {
        Animation fade = AnimationUtils.loadAnimation(
                dropdownRoot.getContext(), R.anim.tegula_dropdown_fade_out);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                list.setVisibility(View.GONE);
                list.clearAnimation();
            }
        });
        list.startAnimation(fade);
        if (chevron != null) chevron.setRotation(0);
    }

    /**
     * Sets the selected item by index (1, 2, or 3).
     * Updates the trigger label and marks the item as activated.
     */
    public static void setSelected(View dropdownRoot, int itemIndex) {
        TextView label = dropdownRoot.findViewById(R.id.tegula_dropdown_label);
        TextView item = dropdownRoot.findViewById(getItemId(itemIndex));
        if (label == null || item == null) return;

        label.setText(item.getText());
        label.setTextColor(dropdownRoot.getContext().getResources()
                .getColor(com.venddair.tegula.colors.R.color.tegula_accent));

        // Clear previous selection
        for (int id : new int[]{R.id.tegula_dropdown_item_1,
                R.id.tegula_dropdown_item_2, R.id.tegula_dropdown_item_3}) {
            TextView v = dropdownRoot.findViewById(id);
            if (v != null) {
                v.setActivated(false);
                v.setTextColor(dropdownRoot.getContext().getResources()
                        .getColor(com.venddair.tegula.colors.R.color.tegula_text_primary));
            }
        }

        item.setActivated(true);
        item.setTextColor(dropdownRoot.getContext().getResources()
                .getColor(android.R.color.white));
    }

    private static int getItemId(int index) {
        switch (index) {
            case 1: return R.id.tegula_dropdown_item_1;
            case 2: return R.id.tegula_dropdown_item_2;
            case 3: return R.id.tegula_dropdown_item_3;
            default: return 0;
        }
    }
}
```

- [ ] **Step 2: Create `dropdowns/src/main/res/values/styles.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Widget.Tegula.Dropdown" parent="@android:style/Widget">
        <item name="android:background">@drawable/tegula_dropdown_bg</item>
        <item name="android:elevation">2dp</item>
    </style>

    <style name="Widget.Tegula.Dropdown.Trigger">
        <item name="android:minHeight">48dp</item>
        <item name="android:paddingStart">16dp</item>
        <item name="android:paddingEnd">16dp</item>
    </style>

    <style name="Widget.Tegula.Dropdown.Item">
        <item name="android:minHeight">48dp</item>
        <item name="android:paddingStart">16dp</item>
        <item name="android:paddingEnd">16dp</item>
        <item name="android:textSize">14sp</item>
        <item name="android:textColor">@color/tegula_text_primary</item>
    </style>
</resources>
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew :dropdowns:compileDebugJavaWithJavac 2>&1 | tail -3`
Expected: BUILD SUCCESSFUL, no errors

- [ ] **Step 4: Commit**

```bash
git add dropdowns/src/main/java/ dropdowns/src/main/res/values/styles.xml
git commit -m "feat: add TegulaDropdown helper and styles"
```

---

### Task 6: Update README and verify full build

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Add dropdowns to README module table**

In the Modules table, add a row after `:core`:

```markdown
| `:dropdowns` | Animated dropdown menu picker |
```

- [ ] **Step 2: Add dropdown usage section to README**

Add after the Java utilities section:

```markdown
### Dropdown menu

```xml
<!-- In settings.gradle -->
include ':Tegula:dropdowns'

<!-- In app/build.gradle -->
implementation project(':Tegula:dropdowns')

<!-- In your layout -->
<include layout="@layout/tegula_dropdown"
    android:id="@+id/dropdown"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```java
import com.venddair.tegula.dropdowns.TegulaDropdown;

// Set item text (customize the 3 options)
TextView item1 = findViewById(R.id.tegula_dropdown_item_1);
item1.setText("Option A");
// ... repeat for item2, item3

// Toggle open/close
findViewById(R.id.tegula_dropdown_trigger).setOnClickListener(v ->
    TegulaDropdown.toggle(findViewById(R.id.dropdown)));

// Handle item selection
findViewById(R.id.tegula_dropdown_item_1).setOnClickListener(v -> {
    TegulaDropdown.setSelected(findViewById(R.id.dropdown), 1);
    TegulaDropdown.close(findViewById(R.id.dropdown),
        findViewById(R.id.tegula_dropdown_list), null);
});
```

- Pure platform APIs — no androidx dependencies (minSdk 21)
```

- [ ] **Step 3: Full project build**

Run: `./gradlew assembleDebug 2>&1 | tail -5`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add README.md
git commit -m "docs: add dropdown module to README"
```
