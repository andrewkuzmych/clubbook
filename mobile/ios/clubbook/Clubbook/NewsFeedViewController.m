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

    [self.newsFeedTable initializeNewsTableType:self.type objectId:self.newsObjectId andParentViewCntroller:(UIViewController*)self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
