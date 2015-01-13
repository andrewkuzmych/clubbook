//
//  ClubPhotGalleryCollectionViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "BaseViewController.h"
#import "Place.h"
#import "EBPhotoPagesDelegate.h"
#import "EBPhotoPagesDataSource.h"

@interface ClubPhotGalleryCollectionViewController : BaseViewController<UICollectionViewDelegate, UICollectionViewDataSource, EBPhotoPagesDelegate, EBPhotoPagesDataSource>

@property (strong, nonatomic) NSMutableArray* photoArray;
@property (strong, nonatomic) Place* place;

@end
