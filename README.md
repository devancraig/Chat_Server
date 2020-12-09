# Project 1: Chat Server

* Authors: Devan Craig, Desmond Porth
* Team#: 8
* Class: CS455 Section 001
* Semester: Spring 2020

# Overview:
This program uses a Socket server and client to create a chat server that can be used by multiple users/clients.

# Link to video:
[https://drive.google.com/open?id=185vvVmVexT4Cs9JZy7uiz4BexiHaxTcS](https://drive.google.com/open?id=185vvVmVexT4Cs9JZy7uiz4BexiHaxTcS)

# File/Folder Manifest
Folder:

* chat-server

Files:

* chat-server/Channel.java
* chat-server/ChatClient.java
* chat-server/ChatServer.java
* chat-server/ClientInfo.java
* chat-server/Command.java
* chat-server/Connection.java
* chat-server/Makefile
* chat-server/Message.java
* chat-server/MsgCommand.java
* chat-server/Nick.java
* chat-server/TimerStart.java
 
# Compiling and Using:

Our ports: 5136-5140

#### Compile Files:

```bash
make
```
#### Running the ChatServer:
Debug 0: Only shows errors

Debug 1: Shows all events other then errors
```bash
java ChatServer -p <port#> -d <debug>
```
#### Running the ChatClient:
 
```bash
java ChatClient -p <port#>
```
# Program Design:
#### ChatServer.java
The server is designed to be multi-threaded meaning each client has it’s own thread and the
main thread distributes data between the client threads.

#### ChatClient.java The client uses an IRC protocol to communicate back and forth with the
server. It also will broadcast messages to other clients if they are in a specified channel.

# Observations/Reflection:
When approaching this project our first idea was to create a single-threaded server that would
communicate back and forth with the server. This would also include all of the working commands
minus broadcasting (since it was not working at first) and channels. There were two issues with
doing this, first was that we did a lot of up front work to find out the challenges of creating
a single-threaded server. This made us change our server from single-threaded to multi-threaded,
which meant quite a bit of code refactoring to get back to what was previously working.

However, the most challenging part of this project for us was broadcasting the messages between
clients. We ran into an issue where one client would be stuck at a specific state while the
other would be sending a message. After the state was executed the message was there, but was
not a working solution. That’s when we decided to create a seperate thread for messaging to
bypass this issue. Once working we again had to do some code refactoring. Moral of this story
is to fix an issue when it comes up or test to make sure what you think is working actually
is. This methodology would have saved us quite a bit of time, and is a method we will use going
into the future.

# Parts Worked On:
#### Devan:
* Multi-threaded
* IRC Protocol

#### Desmond:
* Broadcasting
* IRC Protocol

# Testing:

The main way that we were testing this project was compiling and running the server and client to try running the new
implementation. This would help us gage the changes that we made. We also would use print statements in the server or the
client to make sure we were hitting a certain point. This was also used to make sure we were getting the right
objects/variables back. If there was something that a print statement couldn't help us with we would use the debugger
(eclipse/vs code). 
 


 
