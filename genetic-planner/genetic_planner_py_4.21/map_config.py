class MapConfig:
    width = 500
    height = 500
    obstacle = [
        # {"shape": "rectangle", "center": (60, 70), "width": 30, "height": 40},
        {"shape": "rectangle", "center": (350, 370), "width": 20, "height": 50},
        {"shape": "rectangle", "center": (300, 70), "width": 30, "height": 40},
        {"shape": "rectangle", "center": (100, 370), "width": 20, "height": 50},
        {"shape": "circle", "center": (250, 250), "radius": 50},
        {"shape": "circle", "center": (50, 150), "radius": 30},
        {"shape": "circle", "center": (170, 110), "radius": 20},
        {"shape": "circle", "center": (130, 300), "radius": 30},
        {"shape": "circle", "center": (300, 130), "radius": 30},
    ]
    start = (50, 100)
    end = (400, 400)
