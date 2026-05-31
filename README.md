# Tegula

A modular Android UI component library. Pick exactly what you need — no bloat.

## Modules

| Module | What it provides |
|--------|-----------------|
| `:colors` | 9 colors (day/night/Android 12+ dynamic) |
| `:cards` | Rounded card background drawable |
| `:toggles` | Toggle switch drawable (state selector) |
| `:seekbars` | Custom SeekBar track + thumb |
| `:themes` | Light/dark app themes |
| `:icons` | Warning circle + bypass icon |
| `:buttons` | Rounded button drawable + style |
| `:core` | Java utilities (setupEdgeToEdge) |
| `:dropdowns` | Animated dropdown menu picker |
| `:dialogs` | Centered modal dialog with icon+text rows |

## Usage

### 1. Clone into your project

```bash
cd YourApp/
git clone https://github.com/Victoria-Freeman/Tegula.git
# or symlink if you already have it elsewhere:
ln -s /path/to/Tegula ./Tegula
```

### 2. Include modules in `settings.gradle`

```groovy
include ':Tegula:colors'
include ':Tegula:core'
// ... add only what you need
```

### 3. Add dependencies in `app/build.gradle`

```groovy
dependencies {
    implementation project(':Tegula:colors')     // required by all others
    implementation project(':Tegula:core')       // Java utilities
    implementation project(':Tegula:cards')
    implementation project(':Tegula:buttons')
}
```

### 4. Use the resources

All resources use the `tegula_` prefix to avoid collisions:

```xml
<!-- Colors -->
@color/tegula_bg_root
@color/tegula_bg_card
@color/tegula_text_primary
@color/tegula_text_secondary
@color/tegula_accent

<!-- Drawables -->
@drawable/tegula_card_bg
@drawable/tegula_toggle_bg
@drawable/tegula_seekbar_track
@drawable/tegula_seekbar_thumb
@drawable/tegula_button_bg

<!-- Styles -->
style="@style/Widget.Tegula.Button"

<!-- Themes -->
parent="Theme.Tegula.Light"   <!-- light theme -->
parent="Theme.Tegula"         <!-- dark theme -->
```

### Java utilities

```java
import com.venddair.tegula.core.TegulaEdgeToEdge;

// In your Activity onCreate(), after setContentView():
TegulaEdgeToEdge.setupEdgeToEdge(this);
```

- `setupEdgeToEdge(Activity)` — one call: transparent bars, edge-to-edge layout, automatic content inset padding, and light/dark bar icons
- Pure platform APIs — no androidx dependencies (minSdk 21)

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

// Populate items (any number — 3, 10, 100...)
String[] options = {"Option A", "Option B", "Option C"};
TegulaDropdown.setItems(findViewById(R.id.dropdown), options);

// Handle item selection (popup closes automatically)
TegulaDropdown.setOnItemSelectedListener(findViewById(R.id.dropdown), (index, text) -> {
    TegulaDropdown.setSelected(findViewById(R.id.dropdown), index);
});

// Toggle open/close
findViewById(R.id.tegula_dropdown_trigger).setOnClickListener(v ->
    TegulaDropdown.toggle(findViewById(R.id.dropdown)));
```

- Pure platform APIs — no androidx dependencies (minSdk 21)

### Dialog

```groovy
include ':Tegula:dialogs'
implementation project(':Tegula:dialogs')
```

```java
import com.venddair.tegula.dialogs.TegulaDialog;

TegulaDialog.Item[] items = {
    new TegulaDialog.Item(R.drawable.ic_settings, "Settings"),
    new TegulaDialog.Item(R.drawable.ic_about, "About"),
};
TegulaDialog.show(this, "Options", items, (index, text) -> {
    // row tapped, dialog auto-dismissed
});
```

- Pure platform APIs — no androidx dependencies (minSdk 21)
