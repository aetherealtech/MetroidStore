# Metroid Store

If you've been following the adventures of Samus Aran, you know she goes to great pains to obtain all the various upgrades for her power suit.  She has to find them anywhere from the labyrinthine caverns of Zebes, to the evil doppelganger dimension of Aether, and even a Federation biological space laboratory populated with hideous gravity manipulating monstrosities (but there's *nothing* sinister going on there, that's just a conspiracy theory).

Apparently Samus doesn't realize that you  can just buy all of this stuff at the Metroid Store.  Or... maybe she *does* know that, but she's just really, really, really poor (I mean, she keeps bounty hunting, how many Mother Brain self-detonations does it take to retire in this galaxy?).

The purpose of this codebase is to demonstrate by example my knowledge and skills as a software engineer.  If you're considering hiring me and want to get an idea of how I will contribute to your project, what you see here will give you a much better idea than live technical interviews, canned coding exercise, or other similar techniques.  Those are proxies, and pretty poor ones if you ask me.  This is much closer to what you'd actually be hiring me to do.

All the code here is written exclusively by me (full transparency, *some* of it may have been heavily inspired by Stackoverflow answers, modified as needed), to fulfill virtual requirements I created based on my experience with real commercial software.  In addition to demonstrating my technical knowledge and skills, this code also demonstrates my coding style, including my approach to design and architecture.

Currently the code consists only of a native Android app for the customer of the virtual business (the "customer client"), with a minimal backend server embedded into the app (it runs as an embedded server on the same device as the app) to support the client functionality.  This means to run it all you have to do is build and run the app in Android Studio.

As I continue to work on it, I will build a true backend that will run in Docker on the development machine, ported to various tech stacks (Ruby on Rails, ASP.NET, Java Spring, Nest.js, etc.), port the customer client to different devices and tech stacks (native iOS, .NET MAUI, React Native, Flutter, React Web, etc.), and write additional clients (CRM web site and desktop app, warehouse worker mobile app, delivery driver mobile app).  The purpose of writing components on multiple tech stacks is to demonstrate my subject matter expertise in each one.

## Setup Instructions

Since currently the code consists only of a native Android app with an embedded server for the backend, all you need to do is clone the repo, open the `Client/Android` folder in Android Studio, and run the `CustomerClient` app module on any Android device or emulator (I have so far only designed the UI for phones in portrait mode).  The minimum SDK is 24 (Android 7).