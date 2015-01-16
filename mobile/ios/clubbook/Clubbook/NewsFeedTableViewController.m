//
//  NewsFeedTableViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsFeedTableViewController.h"
#import "NewsFeedCell.h"
#import "NewsData.h"
#import "NewsPhotoCell.h"
#import "DateHelper.h"
#import "EBPhotoPagesController.h"

#define STATIC_HEIGHT 100

@interface NewsFeedTableViewController ()

@end

@implementation NewsFeedTableViewController
{
    NSMutableArray* newsArray;
    NSArray* photoSlideShow;
}

static NSString* NewsFeedCellIdentifier = @"NewsFeedCell";
static NSString* PhotoCellIdentifier = @"NewsPhotoCell";


- (void)viewDidLoad {
    [super viewDidLoad];
    newsArray = [[NSMutableArray alloc] init];
    // Do any additional setup after loading the view.
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    [self.tableView setBackgroundColor:[UIColor colorWithRed:0.980 green:0.839 blue:1.000 alpha:1.000]];
    

//    for (int i = 0; i < 10; ++i) {
        NewsData* news = [[NewsData alloc] init];
        news.nameUser = @"CLUBBOOK";
        news.dateOfPost = [NSDate date];
        news.avatarImageUrl = [UIImage imageNamed:@"avatar_default"];
        news.messageText = @"!!!!!!!!!!!!!!!!!!!!!!";
        news.arrayOfPhotos = [[NSMutableArray alloc] init];
        
        [newsArray addObject:news];
        
        NewsData* news1 = [[NewsData alloc] init];
        news1.nameUser = @"CLUBBOOK";
        news1.dateOfPost = [NSDate date];
        news1.avatarImageUrl = [UIImage imageNamed:@"avatar_default"];
        news1.messageText = @"!!!!!!!!!!!!!!!!!!!!!!";
        news1.arrayOfPhotos = [[NSMutableArray alloc] init];
        [news1.arrayOfPhotos addObject:[UIImage imageNamed:@"menu_background"]];
        [newsArray addObject:news1];
        
        NewsData* news3 = [[NewsData alloc] init];
        news3.nameUser = @"CLUBBOOK";
        news3.dateOfPost = [NSDate date];
        news3.avatarImageUrl = [UIImage imageNamed:@"avatar_default"];
        news3.messageText = @"!!!!!!!!!!!!!!!!!!!!!!";
        news3.arrayOfPhotos = [[NSMutableArray alloc] init];
        [news3.arrayOfPhotos addObject:[UIImage imageNamed:@"menu_background"]];
        [news3.arrayOfPhotos addObject:[UIImage imageNamed:@"photo"]];
        [newsArray addObject:news3];
        
        //dummy data
        NewsData* news2 = [[NewsData alloc] init];
        news2.nameUser = @"CLUBBOOK";
        news2.dateOfPost = [NSDate date];
        news2.avatarImageUrl = [UIImage imageNamed:@"avatar_default"];
        news2.messageText = @"Check out sweet new main photo!";
        news2.arrayOfPhotos = [[NSMutableArray alloc] init];
        [news2.arrayOfPhotos addObject:[UIImage imageNamed:@"menu_background"]];
        [news2.arrayOfPhotos addObject:[UIImage imageNamed:@"photo"]];
        [news2.arrayOfPhotos addObject:[UIImage imageNamed:@"news"]];
        [newsArray addObject:news2];
    
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
    
    return [newsArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NewsFeedCell *cell = [tableView dequeueReusableCellWithIdentifier:NewsFeedCellIdentifier];
    if (cell == nil) {
        cell = [[NewsFeedCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:NewsFeedCellIdentifier];
    }
    
    NewsData* news = [newsArray objectAtIndex:indexPath.row];
    
    [cell.avatarImage setImage:news.avatarImageUrl];
    [cell.nameLabel setText:news.nameUser];
    
    NSString* date = [[DateHelper sharedSingleton] get24hTime:news.dateOfPost];
    [cell.timeLabel setText:date];
    [cell.contentView setBackgroundColor:self.tableView.backgroundColor];
    [cell.newsText setText:news.messageText];
    [cell.photosView setHidden:YES];
    if ([news.arrayOfPhotos count] > 0) {
        [cell.photosView setHidden:NO];
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsData* news = [newsArray objectAtIndex:indexPath.row];
    NewsFeedCell *cell = [tableView dequeueReusableCellWithIdentifier:NewsFeedCellIdentifier];
    cell.newsText.text = news.messageText;
    CGSize maximumLabelSize = CGSizeMake(cell.newsText.frame.size.width, 9999);
    CGSize expectedLabelSize = [cell.newsText sizeThatFits:maximumLabelSize];

    CGFloat height = STATIC_HEIGHT + expectedLabelSize.height;
    
    if ([news.arrayOfPhotos count] > 0) {
        height += cell.photosView.frame.size.height;
    }
    
    return height;
}

-(void)tableView:(UITableView *)tableView willDisplayCell:(NewsFeedCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    [cell setCollectionViewDataSourceDelegate:self index:indexPath.row];
}

-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    int tag = (int)collectionView.tag;
    NewsData* news = [newsArray objectAtIndex:tag];
    
    return [news.arrayOfPhotos count];
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NewsPhotoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:PhotoCellIdentifier forIndexPath:indexPath];
    int tag = (int)collectionView.tag;
    NewsData* news = [newsArray objectAtIndex:tag];
    [cell.photoImageView setImage:[news.arrayOfPhotos objectAtIndex:indexPath.item]];
    cell.photoImageView.contentMode = UIViewContentModeScaleAspectFill;
    return cell;
    
}

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    int tag = (int)collectionView.tag;
    NewsData* news = [newsArray objectAtIndex:tag];
    photoSlideShow = news.arrayOfPhotos;
    NSInteger itemClicked = indexPath.item;
    
    EBPhotoPagesController *photoPagesController = [[EBPhotoPagesController alloc]
                                                    initWithDataSource:self delegate:self photoAtIndex:itemClicked ];
    
    [self presentViewController:photoPagesController animated:YES completion:nil];
    
}

#pragma mark - Image Datasource methods
- (UIImage *)photoPagesController:(EBPhotoPagesController *)controller
                     imageAtIndex:(NSInteger)index {
    return [photoSlideShow objectAtIndex:index];
}

- (void)photoPagesController:(EBPhotoPagesController *)controller
                imageAtIndex:(NSInteger)index
           completionHandler:(void(^)(UIImage *image))handler {
    
}

- (BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController
    shouldExpectPhotoAtIndex:(NSInteger)index {
    if (index < [photoSlideShow count]) {
        return YES;
    }
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowCommentingForPhotoAtIndex:(NSInteger)index {
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowReportForPhotoAtIndex:(NSInteger)index {
    return NO;
}

-(BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController shouldAllowMiscActionsForPhotoAtIndex:(NSInteger)index {
    return NO;
}


@end
