# fuse-launcher

Start selected child containers automatically when root container starts.

com.github.yuruki.FuseLauncher is an SCR component with one parameter: ChildPattern. Child containers whose names match ChildPattern regex will have their lifecycles tied to FuseLauncher component's lifecycle. ChildPattern's default value is "^(broker|camel).*$" which matches any child container name that starts with "broker" or "camel".

## Usage

1. Add mvn:com.github.yuruki/fuse-launcher to a Fabric profile.
2. Override ChildPattern in com.github.yuruki.FuseLauncher.properties in the profile if necessary.
3. Assign the profile to a root container.

Now, when root container starts and FabricService becomes available FuseLauncher will start the matching child containers automatically.

Note that osgi:shutdown on root doesn't stop the child containers. For that you should run scr:deactivate com.github.yuruki.FuseLauncher manually.
