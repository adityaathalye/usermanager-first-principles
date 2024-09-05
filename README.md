# Example Web Application in Clojure
This app is a stripped-down variant of [Sean Corfield's usermanager-example](https://github.com/seancorfield/usermanager-example) project (synced as of [commit SHA 2a9cf63](https://github.com/seancorfield/usermanager-example/tree/2a9cf635cf255bf223486bc9e907a02435c7201c)).

## Why?
Sean's original "User Manager" example project and its variants (including this one), aim to demystify **"How to construct a Clojure web application using only libraries?"**.

"Composition over inheritance", "Libraries over frameworks", and "Data orientation" feature prominently in the Clojure world's canonical mental model of programming, including programming the web. Absent some of these key intuitions, even experienced developers who are new to Clojure tend to struggle to build apps using libraries. The "User Manager" collective of demos aim to address this specific challenge. They do *not* aspire to teach web development best practices. To that end, all "User Manager" examples are small, simplified, "Web 1.0" style apps.

## How?
This project expands upon the "from first principles" primer I wrote down in "*[Clojuring the web application stack: Meditation One](https://www.evalapply.org/posts/clojure-web-app-from-scratch/index.html)*". I chose to use Sean's original demo app as a specification because:
- My favourite learning technique is to try and solve the same problem "in my own words" at least two different ways. Sean's "User Manager" and its spin-offs have become a nice little collective playground to do that.
- The collective pooling of ideas benefits everybody. I thought it would be fun to add to the "User Manager" collective.
- And I hope it combines with all the other learning resources out there to create a graded path for the Clojure newcomer.

If nothing else, it exists to scratch one's own itch... I like barebones explanations and love going down "wait, but why?" rabbit holes and tangents.

You see, all the other "User Manager" demos, though simple, are built with libraries used by Clojure professionals in real-world production web apps. So, if libraries are a precursor to one's (custom) web stack (or framework)... what would be the precursor to libraries? No libraries :)

# Reading notes
Keep handy the [original User Manager](https://github.com/seancorfield/usermanager-example)", and [my blog post](https://www.evalapply.org/posts/clojure-web-app-from-scratch/index.html), for side-by-side reference as you work through this codebase.

If anything is unclear, or in error, please feel free to open an issue (but please don't change the structure of the code).

## Follow the commit history

I have crafted it to help the reader observe the piece-by-piece "making-of" of the app. Consult the README *as of the commit in question* for usage instructions relevant to that point in development.

## Take what is useful, discard the REST

I think we learn better by progressively going from approximate coarse-grained mental models to high-fidelity ones. As such, some deliberate simplifications may annoy web professionals (like [using GET to delete](https://github.com/adityaathalye/usermanager-first-principles/commit/200b378146d4d6fdad4218bf950a61ac20c35b86)). One trusts learners to pick up the "right" ways to do things from the sum total of their studies, experiments, colleagues, and mentors.

When learning, it's sometimes good to be RESTless ;)

## How to explore the code

What to expect, if you work forward from the very first commit:
- From the get go, you should be able to issue `curl` requests to the app.
  ```
  curl localhost:3000
  curl -XPOST localhost:3000/some/path?q=somethingsomething
  curl -XDELETE localhost:3000/foo/bar/baz
  ```
  - Bit by bit the app will get more selective and/or demanding about responses.
  - Try playing with request combinations.
  - Read the code to decide what requests to craft, as the handlers and APIs evolve.
- Use the REPL and start playing with the code from the very first commit.
  - Use the "[Rich Comment Blocks](https://betweentwoparens.com/blog/rich-comment-blocks/)" at the bottom of each namespace.
  - The `usermanager.main` namespace should always have some current-as-of-the-commit way to start/stop the server process (and/or current state of the app).
  - Keep a look out for new library dependencies, introduced in the deps.edn. If you spot a new namespace in code, that is not a `usermanager.*` namespace, that's a clue.
  - You will have to restart the REPL to get new dependencies as they appear in the source. The start/stop utilities in `usermanager.main` should ease restoring your server, after restarting the REPL.
- Tests should pass at each commit. Let these guide your understanding too.
  - For the initial few commits, the app will simply echo back some information it finds in the request.
  - At some point requests will affect the database. Now, the app should create a SQLite database somewhere. Check either the root of the project, or under the project's `dev/` directory (you may have to manually create it).
  - The app set up should populate two tables (`department` and `addressbook`).
  - Over the last few commits, a web UI should start to come alive.
- Feel free to query/modify/inspect the `dev` database...
  - Via the app's APIs, the UI, as well as directly.
  - You can easily restore the initial state. Simply delete the file on disk. Then recreate it using our handy dev utilities.
- Finally, at the very end, you will have a fairly "done" User Manager web application.
  - This will demonstrate a conventional build setup, using Clojure CLI tools.
  - At this end state, try out the tools-build method of running the app and building it for local standalone use as well as for server deployment.

# Library, API, and Software Design Choices

## Libraries
Like I mentioned, I've subtracted as many libraries as I could, without compromising fidelity to the original project's design. The exception is, any form of host interop between our web app and the outside world. I've assumed pre-existing solutions (libraries) for those needs (I have to draw a boundary somewhere!). Also some creature comfort utilities that aren't central to the theme of this first-principles explanation.

Hark back to the blog post to get a better picture of where and why I've drawn this boundary.

Specifically, I've used:
- Bare essentials like adapters for Jetty, SQLite, and HTML rendering.
- Some ring middleware that handles HTML form data.
- Everything else is hand-rolled using only the Clojure standard library.

## API / Domain specifications
To stay true to Sean's specification for usermanager's API, domain model, and core "business logic", I have straight-copied parts of his usermanager-example source:
- The URI scheme
- All handler functions (controllers) as-found
- All model functions as-found

## Software design intent
To reinforce the idea of composing moving parts using plain Clojure data, I have crafted my code to use the design choices made by Sean (e.g. injecting the name of the view in request context, for later use by HTML rendering logic). Likewise, to stay true to the Ring specification, all self-written Ring utilities and middleware follow the Ring spec. Replacing them with Ring-provided originals should be straightforward.

If you choose to write your own variant, I suggest following suit.

It should be obvious by now, but I will state it anyway... The stripped down app is not at all robust to real-world demands because it omits nearly all the libraries used by the other "User Manager" examples, that do all the "production grade" heavy lifting for us. Even those apps will need some work done on them (design reviews, integration testing, security checks etc.), if they are to qualify for real-world deployment.
# Requirements
Same as Sean's usermanager-example project.

- This example assumes that you have a recent version of the [Clojure CLI](https://clojure.org/guides/deps_and_cli) installed (at least 1.10.3.933), and provides a `deps.edn` file.
- Clojure 1.10 (or later) is required. The "model" of this example app uses namespace-qualified keys in hash maps. It uses [next.jdbc](https://cljdoc.org/d/seancorfield/next.jdbc) -- the "next generation" JDBC library for Clojure -- which produces namespace-qualified hash maps from result sets.
- The`tools.build` library to use commands from the `build.clj` file. It is included via the `:build` alias of the `deps.edn` file. Clojure-cli-using projects use such a `build.clj` file by convention, to provide standard and custom project build functionality. Project skeleton setup tools typically auto-generate this file. I've copied it over from Sean's project.
- **Note:** I have not tested any of this on Windows or MacOS machines. Given that this app is as vanilla as they come, I believe it should "just work" on Linux, MacOS, and WSL under windows, as long as requirements are satisfied.

# Usage

Clone the repo, `cd` into it, then follow any of the methods below to try out the app and/or deploy it. Note that **the resulting app is NOT fit for production deployment.** Feel free to deploy it, of course, but expose it to the Public Internet only on a throwaway server instance.

## Run the tests

Run the tests this way, from the root of the project.

```
clojure -T:build test
```

This uses the `:build` alias to load the `build.clj` file, based on [`tools.build`](https://clojure.org/guides/tools_build), and run the `test` task.

Hopefully the tests pass! You should see something like this:
```
Running tests in #{"test"}

[ Many lines of test runner log messages. ]

Ran 11 tests containing 37 assertions.
0 failures, 0 errors.
```

Note about the log messages:
- You will see lots of debug messages. I would normally exclude most of these from my day-to-day test output. Here, the extra logging is for teaching purposes.
- At the start of the test run, you may also see a few (annoying) messages about the "SLF4J" logger implementation. You may safely ignore these.

## Run the Application

You may run the app at any point in the commit history of this project. *However*, the functionality available will only match whatever is built up unto that commit.

Start the app and point your browser to [http://localhost:3000](http://localhost:3000).
```
clojure -M -m usermanager.main
```

If that port is in use, start it on a different port. For example, port 3100:

```
clojure -M -m usermanager.main 3100
```

## Run the Application in REPL or via Editor

Start REPL

```
clj -M:dev:test
```

Once REPL starts, start the server on the default port (port 3000):

```clj
user=> (require 'usermanager.main)  ; load the code
user=> (in-ns 'usermanager.main)    ; move to the namespace
usermanager.main=> (-main)          ; or some other port (-main 8080)
```

Point your browser to the appropriate URL [http://localhost:PORTNUMBER](http://localhost:PORTNUMBER).

Use the `dev` and `test` profiles when you run the REPL, whether standalone, or via your favourite editor.

Then, eval/apply away!

## Build an Uberjar for Server Deployment

For server deployment, you typically want to build an "uberjar" -- a `.jar` file that contains Clojure itself and all of the code from your application and its dependencies, so that you can run it with the `java -jar` command. (But like I stated earlier, this project is not production software. So deploy it only to throwaway server environments.)

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

Building JAR...
```

The `target` folder will be created if it doesn't exist and it will include a `classes` folder containing all of the compiled Clojure source code from the `usermanager` application _and all of its dependencies_ including Clojure itself:

```
ls target/classes/
hiccup  hiccup2  public  ring  usermanager
```

It will also include the standalone `.jar` file which you can run like this:

```
java -jar target/usermanager/example-standalone.jar
```

This should behave the same as the _Run the Application_ example above.

This JAR file can be deployed to any server that have Java installed and run with no other external dependencies or files.

# Stuff I Need To Do

I might demo how to replace each hand-rolled piece using production Clojure libraries.

But maybe you can do it in your own words, as self-assigned homework! :)

Compare and contrast with those other usermanager-example projects, for clues.

May the Source be with You!

# License & Copyright

Copyright (c) 2015-2024 Sean Corfield.
Copyright (c) 2024 Aditya Athalye.

Distributed under the Apache Source License 2.0.
