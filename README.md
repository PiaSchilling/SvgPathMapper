# SvgPathMapper
Simple program to create material icons from svg files by mapping xml code to kotlin code

### What it can do - Example


The following svg code is used as the input: (the icon looks like this ![android_black_24dp](https://user-images.githubusercontent.com/96486990/156072201-e23e27cc-065c-4471-8002-ec3d9552c595.svg))

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg width="100%" height="100%" viewBox="0 0 24 24" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;">
    <path id="android" d="M17.532,20.58C16.98,20.58 16.524,20.136 16.524,19.584C16.524,19.032 16.98,18.6 17.532,18.6C18.084,18.6 18.54,19.032 18.54,19.584C18.54,20.136 18.084,20.58 17.532,20.58M6.492,20.58C5.94,20.58 5.484,20.136 5.484,19.584C5.484,19.032 5.94,18.6 6.492,18.6C7.044,18.6 7.488,19.032 7.488,19.584C7.488,20.136 7.044,20.58 6.492,20.58M17.892,14.568L19.896,11.112C20.004,10.908 19.932,10.656 19.74,10.548C19.536,10.428 19.284,10.5 19.2,10.704L17.148,14.196C15.54,13.464 13.8,13.08 12,13.092C10.164,13.092 8.4,13.488 6.876,14.184L4.848,10.692C4.74,10.488 4.488,10.416 4.284,10.536C4.08,10.644 4.02,10.896 4.128,11.1L6.12,14.556C2.7,16.428 0.348,19.896 -0,24L24,24C23.664,19.908 21.324,16.44 17.892,14.568Z" style="fill-rule:nonzero;"/>
</svg>
```

The kotlin code created by the program then looks like this:

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.Android: ImageVector
    get() {
        if (_android != null) {
            return _android!!
        }
        _android = materialIcon(name = "Filled.Android") {
            materialPath {
               moveTo(17.532f,20.58f)
               curveTo(16.98f,20.58f,16.524f,20.136f,16.524f,19.584f)
               curveTo(16.524f,19.032f,16.98f,18.6f,17.532f,18.6f)
               curveTo(18.084f,18.6f,18.54f,19.032f,18.54f,19.584f)
               curveTo(18.54f,20.136f,18.084f,20.58f,17.532f,20.58f)
               moveTo(6.492f,20.58f)
               curveTo(5.94f,20.58f,5.484f,20.136f,5.484f,19.584f)
               curveTo(5.484f,19.032f,5.94f,18.6f,6.492f,18.6f)
               curveTo(7.044f,18.6f,7.488f,19.032f,7.488f,19.584f)
               curveTo(7.488f,20.136f,7.044f,20.58f,6.492f,20.58f)
               moveTo(17.892f,14.568f)
               lineTo(19.896f,11.112f)
               curveTo(20.004f,10.908f,19.932f,10.656f,19.74f,10.548f)
               curveTo(19.536f,10.428f,19.284f,10.5f,19.2f,10.704f)
               lineTo(17.148f,14.196f)
               curveTo(15.54f,13.464f,13.8f,13.08f,12f,13.092f)
               curveTo(10.164f,13.092f,8.4f,13.488f,6.876f,14.184f)
               lineTo(4.848f,10.692f)
               curveTo(4.74f,10.488f,4.488f,10.416f,4.284f,10.536f)
               curveTo(4.08f,10.644f,4.02f,10.896f,4.128f,11.1f)
               lineTo(6.12f,14.556f)
               curveTo(2.7f,16.428f,0.348f,19.896f,-0f,24f)
               lineTo(24f,24f)
               curveTo(23.664f,19.908f,21.324f,16.44f,17.892f,14.568f)
               close()

            }
        }
        return _android!!
    }

private var _android: ImageVector? = null
```
A kotlin file is created, which contains the generated code to be able to include it easily into the project. The output file is saved to the same directory as the input svg file. 

Possibly existing matrix transformations are flattened/removed by calculating the path coordinates new based on the matrix values. Translate and other transformations are currently not taken care of. 

### How it works

There are two programm arguments required:

1. the path to the input svg file 
2. the name the material icon should have (will be used to name the variables and the kotlin file)

Just set both of them in the given order as arguments for the main method.
