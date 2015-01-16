//
//  NewsPhotoLayout.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/15/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsPhotoLayout.h"

#define SPACING 2

@implementation NewsPhotoLayout
{
    NSMutableArray* layoutInfo;
}
#pragma mark - Lifecycle

- (id)init
{
    self = [super init];
    if (self) {
        [self setup];
    }
    
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setup];
    }
    
    return self;
}

- (void)setup
{
}
#pragma mark - Layout

- (void)prepareLayout
{
    layoutInfo = [[NSMutableArray alloc] init];
    NSInteger sectionCount = [self.collectionView numberOfSections];
    NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:0];
    
    int width = self.collectionView.frame.size.width;
    int height = self.collectionView.frame.size.height;
    
    for (NSInteger section = 0; section < sectionCount; section++) {
        NSInteger itemCount = [self.collectionView numberOfItemsInSection:section];
        
        if (itemCount == 1) {
            indexPath = [NSIndexPath indexPathForItem:0 inSection:0];
            UICollectionViewLayoutAttributes *itemAttributes =
            [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
            itemAttributes.frame = CGRectMake(0, 0, width, height);
                
            [layoutInfo addObject:itemAttributes];
        }
        else if (itemCount == 2) {
            indexPath = [NSIndexPath indexPathForItem:0 inSection:section];
            UICollectionViewLayoutAttributes *element1 =
            [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
            element1.frame = CGRectMake(0, 0, width/2 - SPACING/2, height);
            
            [layoutInfo addObject:element1];
            
            indexPath = [NSIndexPath indexPathForItem:1 inSection:section];
            UICollectionViewLayoutAttributes *element2 =
            [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
            element2.frame = CGRectMake(element1.frame.size.width + SPACING, 0, width/2 - SPACING/2, height);
            
            [layoutInfo addObject:element2];
        }
        else if (itemCount >= 3) {
            indexPath = [NSIndexPath indexPathForItem:0 inSection:section];
            UICollectionViewLayoutAttributes *element1 =
            [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
            element1.frame = CGRectMake(0, 0, width/2 - SPACING/2, height);
            
            [layoutInfo addObject:element1];
            
            indexPath = [NSIndexPath indexPathForItem:1 inSection:section];
            UICollectionViewLayoutAttributes *element2 =
            [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
            element2.frame = CGRectMake(element1.frame.size.width + SPACING, 0, width/2 - SPACING/2, height/2 - SPACING/2);
            
            [layoutInfo addObject:element2];
            
            indexPath = [NSIndexPath indexPathForItem:2 inSection:section];
            UICollectionViewLayoutAttributes *element3 =
            [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
            element3.frame = CGRectMake(element2.frame.origin.x, element2.frame.size.height + SPACING, width/2 - SPACING/2, height/2 - SPACING/2);
            
            [layoutInfo addObject:element3];
            
            for (int i = 4; i < itemCount; ++i) {
                indexPath = [NSIndexPath indexPathForItem:i inSection:section];
                UICollectionViewLayoutAttributes *element =
                [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
                element.frame = CGRectMake(0,0,0,0);
                
                [layoutInfo addObject:element];
            }

        }
        
    }
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    return layoutInfo;
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)indexPath
{
    return layoutInfo[indexPath.item];
}


@end
