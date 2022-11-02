# Capstone5703
Android Studio Project for RAT test box brands recognition using Camera or Gallery.

## Preparatory work
The application is developed with Android Studio Chipmunk 2021.2.1 Patch 1 and is tested with an Android virtual devices whose model are Pixel XL (API 26) and Pixel XL 2 (API 31). Moreover, the development was processed on Macbook Pro with an M1 Pro chip (ARM) and Surface Pro with an i5-1135G7 chip (x86). Therefore, to ensure the stablity and success in running the application, your computer should install Android Studio with versions above Chipmunk 2021.2.1 Patch 1 and AVD with API above 26.

Android Studio download link: https://developer.android.com/studio

When successfully install Android Studio, please click the Device Manager button on the right side of interface. Then click the Create Device button. The new window will ask you to selet the hardware. Please choose Pixel XL and click Next. The following window will let you select a system image. Choose release name S with API level of 31 and click Next. At last, please click finish and your AVD will be ready.

## Import the project into Android Studio
1. Once you finished setting Android Studio environment and AVD configuration, you can go to project management window and click 'Get from VCS'. Then, paste https://github.com/KwokSC/Capstone5703.git below to the URL textbox and choose the local directory that you would like to save the project to. 
2. Another way to clone this repository is using `git clone https://github.com/KwokSC/Capstone5703.git <local directory>` to manually obtain this project. (Please replace `<local directory>` to your personalized local directory) After successful clone, you can use import this project by going to Android Studio project managment window and click 'Open'. Subsequently, please locate to the directory you choosed before to import the project.
3. When complete the project import, Android Studio will automatically configure dependency and plugins. Please wait until the status checking below the window finished. (Gradle building) Afterwards, you're all set up.

## Running the application on AVD
1. To run the application, please ensure you successfully follow the steps in the previous section, including AVD creation and project import.
2. Simply click the Run - Run app on the top of the window, or press ^(control) + R on Mac/Ctrl + F10 on Windows. Please wait till the gradle building complete and the application will run on the AVD you've installed.

## Package the application and export it as .apk
1. To run the application on a physical device, you need to have a .pem file. Here we provide with such a file and you can directly find it in the project root directory.
2. To package and export the project as .apk file so that it can be installed on Android physical devices, please click the Build - Generate Signed Bundle/APK on the top of the window. Select APK in the popped window and click 'Next'. In the following window, choose the capstone5703.pem file for the key store path attribute. Key store password and key password are all '980518' which key alias is 'capstone5703'. Filling up the blanks, click 'Next'.
3. Then choose the export destination and select 'release' for 'Build Variants'. After that, click 'Finish' and Android Studio will package the project. You can transfer the exported .apk file to Android phone and install it.
