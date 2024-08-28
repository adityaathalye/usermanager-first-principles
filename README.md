# Example Web Application in Clojure

This app is a stripped-down variant of [Sean Corfield's usermanager-example](https://github.com/seancorfield/usermanager-example) project (synced as of [commit SHA 2a9cf63](https://github.com/seancorfield/usermanager-example/tree/2a9cf635cf255bf223486bc9e907a02435c7201c)).

## Why?
I made this project to expand upon my blog post "*[Clojuring the web application stack: Meditation One](https://www.evalapply.org/posts/clojure-web-app-from-scratch/index.html)*". The post attempts a "from first principles" explanation of the web stack as seen in Clojure-land. This project, in turn, sets up the premise for Sean's "User Manager" demo, as well as its variants (see his project README). Those are all built with libraries used by Clojure professionals in real-world production web apps.

I hope the combined effect is a gradual lead in, into the professional Clojurian's world of web programming. If nothing else, it exists to scratch one's own itch... I like barebones explanations and love going down "wait, but why?" rabbit holes and tangents.

## Reading notes
- My variant uses only a small fraction of those dependencies; bare essentials like adapters for Jetty, SQLite, and HTML rendering. Also some ring middleware that handles HTML form data. Everything else is hand-rolled code using only the Clojure standard library. If anything is unclear or in error, please feel free to open an issue (but please don't change the structure of the code).
- **Follow the project commit history**. I have crafted it to help the reader observe the piece-by-piece "making-of", starting from the first commit. The first commit begins with the basic premise set up in my blog post. It adds on from there.
- *The resulting app is NOT fit for production deployment.* Expose it to the Public Internet only on a throwaway server instance, if at all.

## Usage

Clone the repo, `cd` into it, then follow any of the methods below to try out the app.

### Run the tests

Run the tests this way, from the root of the project.

```
clj -X:dev:test
```

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

### Run the Application

You may run the app at any point in the commit history of this project. *However*, the functionality available will only match whatever is built up unto that commit.

Run standalone using:
```
clojure -M -m usermanager.main
```

Connect via a REPL using, and/or your favourite editor, and eval/apply away!
```
clj -M:dev:test
```

From the get go, you should be able to issue `curl` requests. Try playing with request combinations.
```
curl localhost:3000

curl -XPOST localhost:3000/some/path?q=somethingsomething

curl -XDELETE localhost:3000/foo/bar/baz
```
### Explore the code as it develops

Here is roughly what to expect, if you work forward from the very first commit.
- Use the REPL and start playing with the code from the very first commit.
- Use the "[Rich Comment Blocks](https://betweentwoparens.com/blog/rich-comment-blocks/)" at the bottom of each namespace.
- The `usermanager.main` namespace should always have some current-as-of-the-commit way to start/stop the server process (and/or current state of the app).
- Keep a look out for library dependencies (in deps.edn). You may have to restart the REPL to get new dependencies as they appear in the source.
- For the initial few commits, the app will simply echo back some information it finds in the request.
- Bit by bit the app will get more selective and/or demanding about responses.
- Read the code to decide what requests to craft, as the handlers and APIs evolve.
- Tests should pass at each commit. Let these guide your understanding too.
- At some point requests will affect the database. Now, the app should create a SQLite database somewhere (check either the root of the project, or under the `/dev` directory). It should populate two tables (`department` and `addressbook`).
- Over the last few commits, a web UI should start to come alive.
- Finally, at the very, very end, you will have a fairly "done" User Manager web application, complete with a standard build setup using Clojure CLI tools.
- At this end state, try out the tools-build method of running the app and building it for local standalone use as well as for server deployment.

Happy exploring!

# License & Copyright

Copyright (c) 2015-2024 Sean Corfield.
Copyright (c) 2024 Aditya Athalye.

Distributed under the Apache Source License 2.0.
