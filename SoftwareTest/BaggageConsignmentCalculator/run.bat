@echo off
chcp 65001>nul
title Baggage Consignment Calculator Backend
start http://localhost:5000
python app.py