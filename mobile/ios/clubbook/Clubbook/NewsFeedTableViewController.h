//
//  NewsFeedTableViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "BaseTableViewController.h"
#import "EBPhotoPagesController.h"
#import "Place.h"

@interface NewsFeedTableViewController : BaseTableViewController <UICollectionViewDataSource, UICollectionViewDelegate, EBPhotoPagesDelegate, EBPhotoPagesDataSource>

@property (strong, nonatomic) Place* place;

@end
