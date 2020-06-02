# awsum-backend (WIP)

This project consists of multiple modules that together make up the backend for the Awsum.app app.

## Project Layout

- [awsum-api](awsum-api/) - Provides a HTTP API for awsum.app
- [awsum-data](awsum-data/) - Provides a common data access layer that can be used across the project
- [awsum-spotify](awsum-spotify/) Provides a Facade-like interface to communicate with the Spotify API
- [awsum-server](awsum-server/) Provides a STOMP-based WebSocket server for handling groups in realtime
- [awsum-agent](awsum-agent/) A background application that manages groups' queues and listeners' Spotify states.

## Usage

This project uses Maven - simply open it as a Maven project in your favourite IDE and you're ready to go!

More information about specific deployments will be available in the Wiki soon.

## License

This project is available under the MIT license. 

You can check this through the license header in the code or by viewing the [LICENSE](LICENSE) file in this repository.

## Contribute

If you want to contribute to this project simply fork it and create a pull request. 

Any changes should preferably be accompanied by a corresponding test.
