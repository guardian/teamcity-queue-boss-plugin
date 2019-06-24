# teamcity-queue-boss-plugin
TeamCity plugin to automatically re-prioritise the build queue as new builds come in

- First prioritises builds of `master` (aka `<default>`) thereby ensuring continuous deployments go out in a timely fashion (rather than being stuck behind dev branches) 
- Then attempts to fairly distribute the queued builds based on the (top-level) project they belong to, in the hope of avoiding big blocks of time while certain projects hog the queue


#### running locally

- first (if you haven't already) download and unpack TeamCity server (using the exact version where you will be deploying is **highly** recommended)
- checkout the project and run `mvn package tc-sdk:start` (this will start TeamCity server and an agent on your local machine, with the freshly packaged plugin already installed - no copying ZIPs)
- you can connect with the IntelliJ Remote debugger on port `10111` for server and `10112` for the agent.
- you stop the process with `mvn tc-sdk:stop`


#### releasing

_Currently manual!_

Just run `mvn package` and upload the zip from the `target` directory to your TeamCity server (Plugins section of Administration).

#### Some useful resources

- https://confluence.jetbrains.com/display/TCD18/Developing+TeamCity+Plugins
- https://confluence.jetbrains.com/display/TCD18/Getting+Started+with+Plugin+Development
- https://github.com/JetBrains/teamcity-sdk-maven-plugin/wiki/Developing-TeamCity-plugin
- https://github.com/JetBrains/teamcity-sdk-maven-plugin/wiki