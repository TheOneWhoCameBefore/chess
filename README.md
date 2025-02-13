# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[![Sequence Diagram 2]](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjACMwAkmhUR0gcMGfYPlgwj0XUlDC5BfDI5uUALE5O3R6U1G6mA9jG5T6ACFgNdIgBHHxqMCTA4oBAGGAANUu11u90wT2eJTehTYnG4ZRg-UGUBGENWsz681UiygywmUyZGy2O05fQORPJXFeH2EVFE5SgURimSgAApItFYpQEUjYgBKQlPcWiUkybRKFTqcrgsAAVQ68tp9K1RNk8iNalU+uMpQAYkhODBLZQHcAYDowjbgKNtY8jsxfbB3b5OOGiQbHcpnW8xcVJT6Ov6E0TdSo02T2CKqUrZaq1D5jrmnsLKYXiZRykC+iDVGCIVMAKJQbxUo7S2FheQAa3QqJrj3Foo+2W+MD+AGZAcDnR3xt3e9BygOUEOYKPxzBBU90ZjozAAHIQZixnzxxNPf1O9QN-MocrPlPqLsADxU2AEIkk7EnqHxfsaqilCAg6ZNG1rZtodqPkmijfi6HxugoHDXNGOYoaBBbgYa6HQbBKAKPcqTysAVH+shj4QammE6KU2HXJRYCpPhKHvg2dZNhEMoqlAkSqFWDwoQJsDvIU05UjSHT0pMkJclMtFcfUEBjmgKlTPsIHyQ2c4YOUABM-yAgMSmhmMMCqWsfQaakWk6Xpqwno8Z6GOaaAoL+2AoOAKDXD2faGRmREfNJ5R-gBQFoCB0lvsUgktm266QmF24wPKHBqDBxAJTAEAAGYwJQfYahOBFGbJnw5GA5QAKyWb0q6guCG59NlVJ5QVUBFQkSRlRVW5QNVx4gegHAJjNZjxp43h+IE0DsOCMAADIQNESQBGkGRZF8eRyalVLVHUTStAY6jDYCIajLMLJshwBx1UWFJpdZQy2bsjnPUsf0HMl6YiCo5QIDtXryttu1qsiWp8cRyaQaaKAWlaD0oFqTGvixHperhiHyIGwY2WGkZZq8d4Pk+JGQSlYMflTUA8VOkWGMjaGozBe6ZJx1HOfRCa4xhhRYThMAC2zhGc9FxaUuUsNemJEkJiDoMlM2310r99n6Y5zmuUeDmeY2MmzsdTUwBZAK9Dryn64y6lUcbulOwZ3ks1eN4wDTs32vTzGa5motxSggHDQmSPy59VLK2gqvVkKCszqdLwKR17ZdVl43lPeznQEgABeIU1TqZ3GVbvxOAAjCurZrjnm59vnwRUUXpfLFNXu+f5gXBaF43Rxz-Gp1S4eR8BKdx4zWvUlnmUtzl-WqIVU8jeVlXQJNZt1ZbjUtW16VN52PV57l+Vr4NG8lVv4273N8bzUtvj+AEXgoOgW07b4zD7ekTImATLME1oJCo0guybS7PULszQWg3VUHddumltLoEwO9Vg49mypD3PlKAUFqSF0GsXeIiRygAB4jZoLQPkPYswdAQA4JYZsexgbj31O+CGv97gwx4WAeGmoMGjy5i+QhZoBY0VdjQnGQc8bi1Yp6b00ttCkxgNQnShxjhSyon7OMAc6Yo2DunCU4MdFcRlkjQootSj5W4PzKiUiLFIRFnIsWRhWKRGGBAGg5juLaBHkzMecdyhUAgFcdWHD6ryVYYEkk9UQHmWPmiDEhgBY+1vPo1xRi8YhzMWHf8EcEpxLArHEsn43GT2KTPEsc80qL2bufVuMAC4dxIWXKaeZK4JOrguOuDcMqNN6m3YhJcOkpPPH5AKQVMhD3Cl0oJ9UYowCqVHGp9ZolnWbA0s+wzL4DSGokO+Y0qrl3ZhnKuh8YCtTtifTquyL6r3XsVUa28JpnIqs-RaXg36BGlNcTasoYAAHFbIugAYdYBVtOFbMqMCmB8D7C2W6Bo9BmDlk9FwbCSghCeijNIQlShqLaH0MDEwlh1I2GYBBiYzMyBYigpBDDWUjK1CCLAIjER1i3FozAJI4lsicnuLdEojidFVFBnUdIzRlN0n+2ydzYxstygqPkCUoi3KhWlHpWAVlqh5QMVQmI10ijCbqIQAgEFtlLEiLKYrLaLKwVJ0ko8DWJj6l9CRSCDcFR+hepQCcaQG5a5mUXD8JkB04LkwZGpPoOgECgBHNGZS-ImT+svLZfkewYCND3t0g+85bYN39aoH1frbKBuDaG8NUxI0oGTb9VNUx42JobaMP6abbIZtGFmnNEy0m6OvJk+8BjHiiznqHSphSN7qrlh9cpRr0KrOnrWKJYDM6N3ud1PZrSuKd3GXEtODV5x-Hru1Td2cHnNN3akfd3d+0+imQPWZKzh4LNMXOrBISVnTuqau2emyM7bIvUvJpK8r7POGsct5j931HsSdc4+Ozt2PIgzfF599TmdNPF82ar8VoBDuFAbA3B4DkStaMFIgCjqNRhUByotQGiIuRSglyNDATptsgcSmZwLjIDxGgO4LrzaXJPUhkDjSYRwhQIiZEE4vY4n4zcQTBI4MyXnfazFeCcXNnxWQtARLpXoDoQw8lwHOPtv5MeM2NLlUwF5nIFArL5RwHIqy9lhqrGLtRjACRjiBUKuNfjUVfj-RqOJQmWVuj5UEXHYBj9KrxVqtqly7zzoyJ8yc7ZeUrLhaxbcSatiktcsBJS4su1glXOZfc5WZOUk13uo3f6yt5QQ1huw4+fehQENFvPc1oNrXq0dYqqkvxGS9EjpAnFvJzMCnxTWbxW1GnBJzaKQtx8brzbAcGVenKN670fLU6J0yfSz13MvSh69rGDvDd7k+mZIVX3zMW+V5bE9f3rcTJtmJC8JO7b6mhw5m8Tk70OxXC5PSrk3IGafS74GDm31eQ-MHXlaao9mi-H5BHLDokhskGAAApcJSRWWBBbSAEcULaPrvKNUc0V0Wj+pRUZtAgISPABx1AOAEBIZQFmP1t63S3s4O0wQ3TbSS76cM6gnSJmyXMOA+zzn3PecrAAOosBOLAloUJNoKDgAAaUcv1qt7WqW2a4TAAAVsT5zVXHM1dk0IrzNjfPo35SzwVir5EeIJsopLAZJURai1xCbaO0u5NpWY1VwBZ36hsWaZz-XDVxYUX764JWSaSv65F7RpOYsoWm1H5mmfY9lY-fHnlNuvTOdL4awOQrCsAFkmFIFKmEUvMBkgZFSDAPy+O60tHJyOFp0aQKUzV9RnXCaKcs3pFLBAg5mE-tlBoAiDfvcYRmxUoVy7Err-BxXpZ2CyPVdss6kC6yj0-fO6BvZu59yHndmbcv8HelLhh1u3OzSH-L6fyjkbTESfQ6afRNOfWyKWHCEKS-Q-UpYXU-B3c-WrYTAib7WFHoF-F7eJAtE7XrDAmA9HOaUbUnIdMPUdRMIvOzVbGdcvOAr9Bde3TIGrcSOrDbBrLbX7HbbqKTISJ3FEYbI7SHMTW5ZDSEHg9lAA+ad9Og5ZRgrLUYC-K-C2RrczCtAbGANrH4YbLrY9XA8TE3QbM3MwUbdJUggvMdAreLSdXfD7Fdc5I-CrKkagv9V1dgm-UQ5eKkfbdpe9Q9ZQ3Q62U9T-C7b-Pba7Hw+TUbPuaZQeJ7aAOPY-b9PfSJADGnTg2HUIgHBHDDEHd5bQ-NbrXpaHc9LgzI8oJ5dDKDJHLDM2eaTHZad+LwDnT4PcWAYAbAEjQgfTKjSFEBOjeeCBKBGBOBVoYwYRCHeA-Ai3DmcoEAbgPAA1cYo-TVTfWY+YhUL3ILNPaQdEdGQwf0PQAwLRIQHwdCBIxw0JcJcgtA+jfAwQnA62PAh9aI59R7Xqc4+A5IpQupDdUozw8owHRHTDUHfIiHB4o+EQv7OHLI6+IHaDZHW7XDTAIAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
