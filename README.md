# Example Web Application in Clojure

This is a first-principles take on Sean Corfield's usermanager-example project (as of this commit in his repo).

I'm calling this the second part of elucidating a first-principles mental model of Web Application Stack(s) as seen in Clojureland. The first step was: [Clojuring the web application stack: Meditation One](https://www.evalapply.org/posts/clojure-web-app-from-scratch/index.html "Clojuring the web application stack: Meditation One").

Sean made the now-prototypical usermanager-example using using [Component](https://github.com/stuartsierra/component), [Ring](https://github.com/ring-clojure/ring), [Compojure](https://github.com/weavejester/compojure), and [Selmer](https://github.com/yogthos/Selmer), connected to a local SQLite database.

I've used only...
- The very minimum essentials to build out real-world functionality:
  - An adapter for Jetty (ring-jetty-adapter),
  - a JDBC adapter to run SQL queries from Clojure (next-jdbc), and
  - a Java driver for SQLite (xerial/sqlite).
- Two creature comforts:
  - A Clojure data to HTML compiler (hiccup)
  - An HTTP client and a test runner to help with REPL development and to run tests

Sean's repo has links to other variants by him and others.

## Quickstart via Devcontainers or Github Codespaces
If you have configured your Github account, you can start the project without any other setup.  It will open a web-based vscode editor backed by a Github Codespace VM. (Codespaces is Github's hosted Devcontainer solution)

[![Open in Github Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/adityaathalye/usermanager-first-principles)

You can also clone this repo locally, and using vscode (with the devcontainer plugin), and Docker Desktop, run an isolated, fully setup version of this application locally. Open the repo in your editor and run the command `Dev Containers: Open Folder in Container...`.

## FIXME: Requirements

This example assumes that you have a recent version of the [Clojure CLI](https://clojure.org/guides/deps_and_cli) installed (at least 1.10.3.933), and provides a `deps.edn` file, and a `build.clj` file.

Clojure 1.10 (or later) is required. The "model" of this example app uses namespace-qualified keys in hash maps. It uses [next.jdbc](https://cljdoc.org/d/seancorfield/next.jdbc) -- the "next generation" JDBC library for Clojure -- which produces namespace-qualified hash maps from result sets.

## Usage

Clone the repo, `cd` into it, then follow below to _Run the Application_ or _Run the application in REPL_
or _Run the tests_ or _Build an Uberjar_.

### Run the Application
```
clojure -M -m usermanager.main
```

It should create a SQLite database (`usermanager_db`) and populate two tables (`department` and `addressbook`) and start a Jetty instance on port 3000.

If that port is in use, start it on a different port. For example, port 3100:

```
clojure -M -m usermanager.main 3100
```

### Run the Application in REPL

Start REPL

```
$ clj
```

Once REPL starts, start the server as an example on port 8888:

```clj
user=> (require 'usermanager.main)                             ; load the code
user=> (in-ns 'usermanager.main)                               ; move to the namespace
usermanager.main=> (def system (new-system 8888))              ; specify port
usermanager.main=> (alter-var-root #'system component/start)   ; start the server
```

### Run the tests with:

```
clojure -X:dev:test
```

You should see something like this:

```
Running task for: test

Running tests in #{"test"}
2023-01-24 22:31:01.269:INFO::main: Logging initialized @4050ms to org.eclipse.jetty.util.log.StdErrLog

Testing usermanager.model.user-manager-test
Created database and addressbook table!
Populated database with initial data!

Ran 3 tests containing 9 assertions.
0 failures, 0 errors.
```

<!-- This uses the `:build` alias to load the `build.clj` file, based on [`tools.build`](https://clojure.org/guides/tools_build), and run the `test` task. -->

## Build an Uberjar

For production deployment, you typically want to build an "uberjar" -- a `.jar` file that contains Clojure itself and all of the code from your application and its dependencies, so that you can run it with the `java -jar` command.

The `build.clj` file -- mentioned above -- contains a `ci` task that:

* runs all the tests
* cleans up the `target` folder
* compiles the application (sometimes called "AOT compilation")
* produces a standalone `.jar` file

```
clojure -T:build ci
```

That should produce the same output as `test` above, followed by something like:

```
Copying source...

Compiling usermanager.main...
2023-01-24 22:35:37.922:INFO::main: Logging initialized @2581ms to org.eclipse.jetty.util.log.StdErrLog

Building JAR...
```

The `target` folder will be created if it doesn't exist and it will include a `classes` folder containing all of the compiled Clojure source code from the `usermanager` application _and all of its dependencies_ including Clojure itself:

```
$ ls target/classes/
camel_snake_kebab  clout  compojure  instaparse  medley  public  selmer       views
clojure            com    crypto     layouts     next    ring    usermanager
```

It will also include the standalone `.jar` file which you can run like this:

```
java -jar target/usermanager/example-standalone.jar
```

This should behave the same as the _Run the Application_ example above.

This JAR file can be deployed to any server that have Java installed and run with no other external dependencies or files.

## Stuff I Need To Do

* I might add a `datafy`/`nav` example.

# License & Copyright

Copyright (c) 2015-2024 Sean Corfield.
Copyright (c) 2024 Aditya Athalye.

Distributed under the Apache Source License 2.0.
