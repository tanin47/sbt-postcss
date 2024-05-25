sbt-postcss
============

With sbt-postcss, you can execute [Postcss](https://postcss.org/) during Playframework's hot-reload and stage. It works with both `sbt run` and `sbt stage`.

The initial use case is to support Tailwindcss with [sbt-svelte]https://github.com/tanin47/sbt-svelte) in Playframework.

It currently only supports processing only one file for now.

Installation
-------------

Please see the full example in the folder: `test-play-project`.

### 1. Install the plugin

Add the below line to `project/plugins.sbt`:

```
lazy val root =
  Project("plugins", file(".")).aggregate(SbtPostcss).dependsOn(SbtPostcss)
lazy val SbtPostcss = RootProject(uri("https://github.com/tanin47/sbt-postcss.git#<pick_a_commit>"))
```

### 2. Create postcss.config.js

Create the file in the project's root directory. Here's a simple content for configuring Tailwindcss:

```
module.exports = {
  plugins: [
    require('tailwindcss')
  ]
}
```

### 3. Create tailwind.config.js (if using Tailwindcss)

Create the file in the project's root directory. Here's an example:

```
/** @type {import('tailwindcss').Config} */

module.exports = {
  content: [
    './public/**/*.{html,js,css}',
    './app/**/*.{html,ts,js,svelte,css,scss}'
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

### 4. Configure build.sbt

Specify the binary path of postcss-cli, specify the CSS file to be processed, and add postcss to the pipeline stages.

```
postcss / PostcssKeys.binaryFile := {
  if (isWin) {
    (new File(".") / "node_modules" / ".bin" / "postcss.cmd").getAbsolutePath
  } else {
    (new File(".") / "node_modules" / ".bin" / "postcss").getAbsolutePath
  }
},
postcss / PostcssKeys.inputFile := "./public/stylesheets/tailwindbase.css",
Assets / pipelineStages ++= Seq(postcss)
```

### 5. Install postcss-cli and tailwindcss

Run: `npm install -D postcss-cli tailwindcss`

How it works
-------------

sbt-postcss is an sbt-web plugin that executes `postcss-cli` as "an Asset Pipeline task" ([ref](https://github.com/tanin47/sbt-svelte)), not "a Source File task".

It reads the postcss.config.js in the root of the project's directory.


Contributing
-------------

TBD

Publishing
------------

We are not publishing a jar file anymore. You can load the plugin using a URL that points to a specific commit.