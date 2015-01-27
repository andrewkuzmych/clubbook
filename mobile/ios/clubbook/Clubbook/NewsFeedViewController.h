//
//  NewsFeedTableViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "EBPhotoPagesController.h"
#import "Place.h"

@interface NewsFeedViewController : BaseViewController <UICollectionViewDataSource, UICollectionViewDelegate, UITableViewDelegate, UITableViewDataSource, EBPhotoPagesDelegate, EBPhotoPagesDataSource>

@property (strong, nonatomic) NSString* type;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSString* newsObjectId;

@end
