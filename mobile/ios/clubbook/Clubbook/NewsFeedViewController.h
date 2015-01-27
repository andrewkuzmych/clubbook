//
//  NewsFeedTableViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "NewsFeedTableView.h"
#import "EBPhotoPagesController.h"
#import "Place.h"

@interface NewsFeedViewController : BaseViewController

@property (weak, nonatomic) IBOutlet NewsFeedTableView *newsFeedTable;
@property (strong, nonatomic) NSString* type;
@property (strong, nonatomic) NSString* newsObjectId;

@end
