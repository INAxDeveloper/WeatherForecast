# Weather Forecast : Paresh Chauhan Task

Weather Forecast is a simple Android application that allows users to check the current weather and
forecast for different cities. It utilizes the OpenWeather API for retrieving weather data.

## Features

- View current weather conditions for a specified city.
- Save search history for easy access to previously searched cities.

## Installation

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or a physical device.

## Usage

1. Upon launching the app, you'll see a search bar where you can enter the name of the city you want
   to check the weather for.
2. Once you enter a city name and tap on the search button, the app will retrieve and display the
   current weather conditions and the 5-day forecast for that city.
3. You can switch between dark mode and light mode using the settings option in the app.

## Data Binding

The app utilizes Android Data Binding Library to bind UI components in layouts to data sources. This
makes it easier to update UI components with the latest weather data.

## Saved Search History

The app saves the search history of previously searched cities locally on the device. This allows
users to quickly access their recent searches without having to type the city name again.

## API Key

To use the app, you'll need to add your OpenWeather API key to the NDK file in the project. Follow
these steps:

1. Open the `local.properties` file in the root directory of the project.
2. Add a line with the path to your NDK file containing the API key. For example:
   ```properties
   ndk.dir=/path/to/your/ndk/file
