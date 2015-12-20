#!/bin/bash
set -e
aws s3 cp resources/public/css/compiled/app.css s3://konu/notes-frontend/ --acl public-read
aws s3 cp resources/public/js/compiled/app.js s3://konu/notes-frontend/ --acl public-read
aws s3 cp resources/public/js/compiled/notes_frontend.min.js s3://konu/notes-frontend/ --acl public-read
