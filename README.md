# Prova Finale Ingegneria del Software 2022

## Gruppo AM44

- ### 10680314    Azzi Sabrina ([@SabrinAzzi](https://github.com/SabrinAzzi)) <br> sabrina.azzi@mail.polimi.it
- ### 10699692    Berto Paolo ([@Masterpavel2k](https://github.com/Masterpavel2k)) <br> paolo2.berto@mail.polimi.it
- ### 10672722    Caleca Gianvito ([@gianvitocaleca](https://github.com/gianvitocaleca)) <br> gianvito.caleca@mail.polimi.it

| Functionality                   | State |
|:--------------------------------|:-----:|
| Basic rules                     |  🟢   |
| Complete rules                  |  🟢   |
| Socket & communication protocol |  🟢   |
| GUI                             |  🟢   |
| CLI                             |  🟢   |
| Advanced Characters             |  🟢   |
| 4 players games                 |  🔴   |
| Multiple games                  |  🔴   |
| Persistence                     |  🔴   |
| Disconnections resilience       |  🟢   |


| Package  |Class Coverage|Method Coverage| Line Coverage |
|:-----------------|:--------------|:---------------------|:-------------:|
|   model (Global Package)   | 97%  | 91% |      91%      |
| controller (Global Package)  | 85% | 83% |      82%      |


# Launching the game

The game can be launched from a terminal. Both the Client and the Server can be launched from the same JAR. Because of the large filesize of the jar, we uploaded it at the link: https://polimi365-my.sharepoint.com/:u:/g/personal/10672722_polimi_it/ETL1V0XtbMNKk4r9M-BERj4Bhcp5yFUc3UEMlBcAdQvUlQ?e=WkHiJj


## Server
The Server can be launched using the following command
```
java -jar AM44.jar -server -port <port>
```
The parameter -port represents the port used by the server. If omitted, the port is 1337.

## Client - CLI
The CLI can be launched with a CLI interface using the following command
```
java -jar AM44.jar -client <address> -port <port> -cli
```

The parameters -port represents the port of the server

## Client - GUI
The GUI can be launched using the following command
```
java -jar AM44.jar -client <address> -port <port> -gui -scale <scale>
```

The parameter -scale is used to better fit the game to your screen.

## Note:
if port and address are omitted, the port is 1337 and address is 127.0.0.1.

If -cli or -gui are omitted, the default parameter is -cli
