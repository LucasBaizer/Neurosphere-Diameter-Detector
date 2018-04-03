# 1.3 Modifying Data

After data has been generated, there are a few common mistakes with the detection algorithm:
* Organoids do not get detected.
* False positives, or detections of organoids that don't exist.
* Off-center detections.
* Detections which are either bigger or smaller than their organoid.

Luckily, we can efficiently fix all of these problems.

### Creating Detections
If an organoid is not detected, you can easily create a new detection to do so.
On the right side of the output image component, press the *Add* button.
A new detection will be placed in the center of the output image component.
Its size defaults to the average organoid size. You can adjust it using the methods below.

### Removing False Positives
To remove a false positive, click on the detection which is a false positive.
Press the *Delete* key on your keyboard to remove the false positive.

### Moving Detections
To move an off-center detection, simply click on the detection.
Move your mouse to the destination, and click again.

### Resizing Detections
To change the size of a detection, click on the detection.
On your move, scroll wheel up to decrease the size of the detection.
Scroll wheel down to increase the size of the detection.
When you're done changing the size, click again.

### Next up: [1.4 Exporting Data]($LOCAL/ExportingData.md)

--------

### [Table of Contents]($LOCAL/TableOfContents.md)