//
//  NewsFeedTableViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsFeedTableViewController.h"
#import "NewsFeedSingleImageCell.h"
#import "NewsFeedCell.h"

@interface NewsFeedTableViewController ()

@end

@implementation NewsFeedTableViewController
{
    NSMutableArray* newsArray;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    newsArray = [[NSMutableArray alloc] init];
    // Do any additional setup after loading the view.
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;    //count of section
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return 2;//[newsArray count];    //count number of row from counting array hear cataGorry is An Array
}



- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        static NSString *MyIdentifier = @"NewsSingleImageCell";
        NewsFeedSingleImageCell *cell = [tableView dequeueReusableCellWithIdentifier:MyIdentifier forIndexPath:indexPath];
        [cell.avatarImageView setImage:[UIImage imageNamed:@"avatar_default"]];
        [cell.messageTextView setText:@"Hello! Check out new logo!"];
        [cell.nameLabel setText:@"CLUBBOOK"];
        [cell.dateLabel setText:@"12.12.2014"];
        [cell.photoView setImage:[UIImage imageNamed:@"menu_background"]];
        return cell;
    }
    else {
        static NSString *MyIdentifier = @"NewsNoImageCell";
        NewsFeedCell *cell = [tableView dequeueReusableCellWithIdentifier:MyIdentifier forIndexPath:indexPath];
        [cell.avatarImageView setImage:[UIImage imageNamed:@"avatar_default"]];
        [cell.messageTextView setText:@"Hello! Happy new 2015 year"];
        [cell.nameLabel setText:@"CLUBBOOK"];
        [cell.dateLabel setText:@"01.01.2015"];
        return cell;
    }
 
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 0) {
        return 380;
    }
    else {
        return 150;
    }
}

@end
