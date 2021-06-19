#!/bin/bash
coverage run test.py
coverage html
zip -r htmlcov.zip htmlcov