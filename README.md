electionsBiH
============

This repository hosts all data and architecture (front- and back-end) related to the BiH Election Portal. The three permanent branches contain the following information:

- master: all raw and cleaned data and the backend infrastructure
- build: the frontend build (backbone.js website and associated materials)
- gh-pages: the deployed frontend.


#### Requirements

To install dependencies, run these commands from the base folder: `npm install && bower install`

To deploy locally: `grunt serve`

To build: `grunt build`

#### Deployment

`build` branch contains production files. `gh-pages` mirrors the `dist/` folder. Branch from `build` to do development. Pull request into `build`. Then build and push new `/dist` folder to `build` before pushing to `gh-pages`.

Workflow:

1. Work in `app/` folder.

2. If a new file is created, ensure it is captured in `Gruntfile.js`

3. To run locally, run `grunt serve`. Site will run on `localhost:9000`.

4. To build the site, run `grunt build`. This will update the `dist/` folder. Ensure that Gruntfile.js has copied all site files.

5. Push all changes to 'build' branch: both `app/` and `dist/`

6. Run `git subtree push --prefix dist origin gh-pages` to push `dist/` files to `gh-pages` branch.
 
7. If there are problems pushing to `gh-pages` because your version of dist is behind the remote (not pushing to `build` first, using `git revert`, etc), then run this command to force the push to `gh-pages`: ``git push origin `git subtree split --prefix dist build` :gh-pages --force``
