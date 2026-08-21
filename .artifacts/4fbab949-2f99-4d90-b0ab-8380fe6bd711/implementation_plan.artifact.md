# Implementation Plan: Premium Visual Identity & Data Enrichment

This plan aims to elevate the REALTYNOVA app's visual identity to reflect a premium Kenyan real-estate marketplace. We will update the UI components to use the refined color palette and aspect ratios, and enrich the sample data with high-quality, relevant property listings.

## User Review Required

> [!IMPORTANT]
> The sample data will use Unsplash images that represent the "Premium Kenyan" aesthetic. While these are great for development and demos, actual production images should be sourced from verified owners/agents.

## Proposed Changes

### Core Design System & Components

#### [MODIFY] [PropertyCard.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/designsystem/components/PropertyCard.kt)
- Update image aspect ratio from `1.12f` to `1.33f` (4:3) for a more professional look.
- Refine the verified and premium badges to use the design tokens (`VerifiedBlue`, `ChampagneGold`).
- Add subtle shadow and border refinements to the card.

#### [MODIFY] [WelcomeScreen.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/features/auth/WelcomeScreen.kt)
- Replace the generic house image with a cinematic "Nairobi Luxury Home" hero image.
- Adjust the gradient overlay to better blend with the new imagery.
- Refine typography to use `MaterialTheme.typography.displayLarge` for the main headline.

---

### Data Layer

#### [MODIFY] [PropertyRepositoryImpl.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/data/repository/PropertyRepositoryImpl.kt)
- Overhaul sample properties to include:
    - **Runda Luxury Villa** (Hero/Exterior)
    - **Kilimani Penthouse** (Interior/Skyline)
    - **Kilifi Coastal Villa** (Pool/Tropical)
    - **Thika Land Marketplace** (Aerial)
    - **Upper Hill Commercial Space** (Office)
- Implement full `images` lists for each property using the `PropertyImageType` enum.

---

### Feature UI

#### [MODIFY] [PropertyDetailScreen.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/features/home/PropertyDetailScreen.kt)
- Enhance the `PropertyImageHeader` with a parallax effect or a more premium transition.
- Refine the `ClassifiedGallery` to group images by type (Interior, Exterior, etc.) or show them in a more curated grid.
- Improve the `NeighborhoodIntelligence` section with richer icons and layout.

## Verification Plan

### Manual Verification
- Deploy to an emulator/device.
- Verify the **Welcome Screen** looks cinematic and on-brand.
- Check the **Home Screen** (Property Cards) for the new 4:3 aspect ratio and premium badges.
- Navigate to **Property Details** to verify the enriched gallery and neighborhood info.
- Verify the color palette consistency across all screens.
