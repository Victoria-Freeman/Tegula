# Tegula Dropdown Menu Design

**Date**: 2025-01-19
**Module**: `:dropdowns` (new)

## Goal

Add a floating-card-style animated dropdown menu component to Tegula that replaces Android's default Spinner with something visually polished.

## Design Decisions

- **Style:** Floating card — rounded card (12dp corners) that appears below the trigger, matching Tegula's existing card aesthetic
- **Behavior:** Single-select picker — tap to open, tap item to select, dropdown closes
- **Animation:** XML-only via `android:animateLayoutChanges="true"` on the list container + `<alpha>` animation resources for fade. Zero Java required for animation.
- **Module:** New `:dropdowns` module following Tegula's modular pattern. Optional `TegulaDropdown` helper inside `:dropdowns` for convenience toggle.
- **Colors:** Uses existing Tegula palette — `tegula_bg_card` for hover, `tegula_accent` for selected item, `tegula_text_primary`/`secondary` for text.
- **No Java required:** Everything works with XML layout + `setVisibility()` call.

## File Structure

### `:dropdowns` module

| File | Purpose |
|------|---------|
| `build.gradle` | Library module, minSdk 21 |
| `res/layout/tegula_dropdown.xml` | Complete dropdown: trigger (text + chevron) + animated list container (3 items) |
| `res/drawable/tegula_dropdown_bg.xml` | Rounded card background shape (12dp corners, white fill) |
| `res/drawable/tegula_dropdown_item_bg.xml` | Item selector: default transparent, pressed/hovered uses `tegula_bg_card`, selected uses `tegula_accent` tint |
| `res/drawable/tegula_dropdown_chevron.xml` | Vector drawable chevron arrow |
| `res/anim/tegula_dropdown_fade_in.xml` | Alpha 0→1, 200ms |
| `res/anim/tegula_dropdown_fade_out.xml` | Alpha 1→0, 200ms |
| `res/values/styles.xml` | `Widget.Tegula.Dropdown` style, text appearances |
| `src/main/java/.../TegulaDropdown.java` | Optional static helper: `toggle(View dropdown)`, `setSelected(View dropdown, int itemIndex)` |

## Usage

```xml
<include layout="@layout/tegula_dropdown"
    android:id="@+id/dropdown"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

```java
// Toggle open/close
View list = findViewById(R.id.tegula_dropdown_list);
list.setVisibility(list.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);

// Or use helper
TegulaDropdown.toggle(findViewById(R.id.dropdown));
```

## Layout Structure

```
tegula_dropdown.xml
├── LinearLayout (trigger) — id: tegula_dropdown_trigger
│   ├── TextView (label) — id: tegula_dropdown_label
│   └── ImageView (chevron) — id: tegula_dropdown_chevron
└── LinearLayout (dropdown list) — id: tegula_dropdown_list
    ├── animateLayoutChanges="true"
    ├── TextView (item 1) — id: tegula_dropdown_item_1
    ├── TextView (item 2) — id: tegula_dropdown_item_2
    └── TextView (item 3) — id: tegula_dropdown_item_3
```

The list container starts as `visibility="gone"`. When toggled to `visible`, `animateLayoutChanges` animates the height expansion.
