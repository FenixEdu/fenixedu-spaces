bowerInstall: {

  target: {

    // Point to the files that should be updated when
    // you run `grunt bower-install`
    src: [
      'app/views/**/*.html',   // .html support...
      'app/views/**/*.jade',   // .jade support...
      'app/styles/main.scss',  // .scss & .sass support...
      'app/config.yml'         // and .yml & .yaml support out of the box!
    ],

    // Optional:
    // ---------
    cwd: '',
    dependencies: Boolean (default: true),
    devDependencies: Boolean (default: false),
    exclude: [],
    fileTypes: {},
    ignorePath: ''
  }
}