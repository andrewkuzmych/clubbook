//
//  NewsFeedTableViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsFeedViewController.h"

@implementation NewsFeedViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.newsFeedTable.type = self.type;
    self.newsFeedTable.newsObjectId = self.newsObjectId;
    self.newsFeedTable.parentViewController = (UIViewController*)self;
    [self.newsFeedTable loadData];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
