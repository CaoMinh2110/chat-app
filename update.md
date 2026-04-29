# Refactor Dropdown → Popup (Jetpack Compose)

## 🎯 Mục tiêu

Refactor toàn bộ `DropdownMenu` trong màn hình `EditProfileScreen` sang dùng `Popup` để:

- ✅ Điều khiển **vị trí hiển thị chính xác**
- ✅ Dropdown có thể **mở lên hoặc xuống thật sự**
- ✅ Chiều cao dropdown = **max(spaceAbove, spaceBelow)** (tính từ center của TextField)
- ✅ Tránh limitation của `DropdownMenu` (không support open upward đúng)

---

## 🧠 Logic yêu cầu

### 1. Xác định vị trí TextField

```text
top = vị trí top của TextField
height = chiều cao TextField
centerY = top + height / 2
```

---

### 2. Tính khoảng trống

```text
spaceAbove = centerY
spaceBelow = screenHeight - centerY
```

---

### 3. Quyết định

```text
Nếu spaceAbove > spaceBelow:
    → mở dropdown lên trên
    → height = spaceAbove
Ngược lại:
    → mở xuống dưới
    → height = spaceBelow
```

---

## 📐 Minh hoạ

### Case 1: mở xuống

```
|----------------------| ← Top
|
|     TextField
|        ↓
|        ↓ (dropdown)
|        ↓
|
|----------------------| ← Bottom
```

---

### Case 2: mở lên

```
|----------------------| ← Top
|        ↑
|        ↑ (dropdown)
|        ↑
|     TextField
|
|----------------------| ← Bottom
```

---

## 🚨 Vấn đề hiện tại

Đang dùng:

```kotlin
DropdownMenu(...)
```

### Hạn chế:

- ❌ Không control được vị trí chính xác
- ❌ Không mở lên thật (offset chỉ là hack)
- ❌ Dễ bị lệch UI

---

## ✅ Giải pháp: dùng Popup

---

## 🔧 Bước 1: Tạo model position

```kotlin
data class DropdownPosition(
    val offsetX: Int,
    val offsetY: Int,
    val maxHeight: Dp
)
```

---

## 🔧 Bước 2: Hàm tính toán vị trí

```kotlin
@Composable
fun calculateDropdownPosition(
    coords: LayoutCoordinates,
    density: Density,
    configuration: Configuration
): DropdownPosition {

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }

    val position = coords.positionInWindow()

    val top = position.y
    val left = position.x
    val height = coords.size.height

    val centerY = top + height / 2

    val spaceAbove = centerY
    val spaceBelow = screenHeightPx - centerY

    val useAbove = spaceAbove > spaceBelow
    val maxHeightPx = if (useAbove) spaceAbove else spaceBelow

    val offsetYPx = if (useAbove) {
        (top - maxHeightPx)
    } else {
        (top + height)
    }

    return DropdownPosition(
        offsetX = left.toInt(),
        offsetY = offsetYPx.toInt(),
        maxHeight = with(density) { maxHeightPx.toDp() - 16.dp }
    )
}
```

---

## 🔧 Bước 3: Custom Dropdown (Popup)

```kotlin
@Composable
fun <T> CustomDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    anchorCoordinates: LayoutCoordinates?,
    items: List<T>,
    itemToString: (T) -> String,
    onItemClick: (T) -> Unit
) {
    if (!expanded || anchorCoordinates == null) return

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val position = calculateDropdownPosition(
        anchorCoordinates,
        density,
        configuration
    )

    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(position.offsetX, position.offsetY),
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .width(with(density) {
                    anchorCoordinates.size.width.toDp()
                })
                .heightIn(max = position.maxHeight)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemToString(item)) },
                        onClick = {
                            onItemClick(item)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}
```

---

## 🔧 Bước 4: Áp dụng vào màn hình

### 1. Thêm state

```kotlin
var countryCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
var cityCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
```

---

### 2. Gắn vào TextField

```kotlin
Modifier.onGloballyPositioned {
    countryCoords = it
}
```

```kotlin
Modifier.onGloballyPositioned {
    cityCoords = it
}
```

---

### 3. Thay DropdownMenu bằng CustomDropdown

#### Country

```kotlin
CustomDropdown(
    expanded = countryMenuExpanded,
    onDismiss = { countryMenuExpanded = false },
    anchorCoordinates = countryCoords,
    items = state.countries,
    itemToString = { it.name.orEmpty() },
    onItemClick = { country ->
        countryName = country.name.orEmpty()
        cityName = ""
        country.code?.let { viewModel.loadCities(it) }
    }
)
```

---

#### City

```kotlin
CustomDropdown(
    expanded = cityMenuExpanded,
    onDismiss = { cityMenuExpanded = false },
    anchorCoordinates = cityCoords,
    items = state.cities,
    itemToString = { it.name.orEmpty() },
    onItemClick = { city ->
        cityName = city.name.orEmpty()
    }
)
```

---

## ✅ Kết quả mong muốn

- Dropdown:
  - Mở lên / xuống đúng logic
  - Không bị overflow màn hình
  - Height adaptive

- UI ổn định hơn `DropdownMenu`
- Dễ mở rộng animation sau này

---

## 🚀 Optional (nice-to-have)

- Thêm animation (fade + slide)
- Dùng `LazyColumn` thay vì `Column + scroll`
- Click outside dismiss nâng cao
- Elevation/shadow đẹp hơn

---

## 📌 Tổng kết

| Tiêu chí       | DropdownMenu | Popup |
| -------------- | ------------ | ----- |
| Mở lên thật    | ❌           | ✅    |
| Control vị trí | ❌           | ✅    |
| UX linh hoạt   | ❌           | ✅    |

👉 Kết luận: **Popup là hướng đúng cho custom dropdown nâng cao**
