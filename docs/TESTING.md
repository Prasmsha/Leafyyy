# Leafly Testing Documentation

## Unit Tests (src/test/)
- UserModelTest — tests user model creation and toMap()
- PlantModelTest — tests plant model and default values
- CareTaskModelTest — tests task done/undone states
- GrowthLogModelTest — tests growth log creation
- ValidationTest — tests email, password, plant name validation

## Instrumented Tests (src/androidTest/)
- NavigationTest — tests splash, login, register navigation
- LoginScreenTest — tests empty fields, Google button
- RegisterScreenTest — tests password mismatch, terms validation

## How to Run
Unit tests: Right-click src/test/ → Run Tests
Instrumented tests: Right-click src/androidTest/ → Run Tests
