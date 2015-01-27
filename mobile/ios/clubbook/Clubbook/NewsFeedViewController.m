//
//  NewsFeedTableViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsFeedViewController.h"
#import "NewsFeedCell.h"
#import "NewsData.h"
#import "NewsPhotoCell.h"
#import "DateHelper.h"
#import "EBPhotoPagesController.h"
#import "CLCloudinary.h"
#import "CLTransformation.h"
#import "Constants.h"
#import "UIImageView+WebCache.h"
#import "SVPullToRefresh.h"

#define STATIC_HEIGHT 100

@interface NewsFeedViewController ()

@end

@implementation NewsFeedViewController
{
    NSMutableArray* newsArray;
    NSArray* photoSlideShowUrls;
    NSMutableDictionary* photoImages;
    NSMutableDictionary* avatarImages;
    UIImage* staticAvatar;
    NSString *accessToken;
    
    BOOL isRefreshingNews;
}

static NSString* NewsFeedCellIdentifier = @"NewsFeedCell";
static NSString* PhotoCellIdentifier = @"NewsPhotoCell";


- (void)viewDidLoad {
    [super viewDidLoad];
    newsArray = [[NSMutableArray alloc] init];
    
    [self.tableView setBackgroundColor:[UIColor colorWithRed:0.980 green:0.839 blue:1.000 alpha:1.000]];
    
    avatarImages = [[NSMutableDictionary alloc] init];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    accessToken = [defaults objectForKey:@"accessToken"];
    
    __weak NewsFeedViewController *weakSelf = self;
    // setup pull-to-refresh
    [self.tableView addPullToRefreshWithActionHandler:^{
        [weakSelf insertRowAtTop];
    }];
    
    // setup infinite scrolling
    [self.tableView addInfiniteScrollingWithActionHandler:^{
        [weakSelf insertRowAtBottom];
    }];
    
    [self loadNews:0 limit:5 refreshing:YES];
}

- (void) loadNews:(int)skip limit:(int)limit refreshing:(BOOL)refreshing {
    isRefreshingNews = refreshing;
   [self._manager retrieveNews:self.type withId:self.newsObjectId accessToken:accessToken skip:skip limit:limit];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) didReceiveNews:(NSArray*) news {
    if ([news count] > 0) {
        if (isRefreshingNews) {
            newsArray = [news mutableCopy];
            isRefreshingNews = NO;
        }
        else {
            for (NewsData* newsObject in news) {
                [newsArray addObject:newsObject];
            }
        }
        
        if ([newsArray count] > 0) {
            [self.tableView reloadData];
        }
    }
    
    [self.tableView.pullToRefreshView stopAnimating];
    [self.tableView.infiniteScrollingView stopAnimating];
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
    
    if ([self.type isEqualToString:@"club"]) {
        if (staticAvatar == nil) {
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @60, @"height": @60}];
            NSString * avatarUrl  = [cloudinary url:news.avatarPath options:@{@"transformation": transformation}];
            
            NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:avatarUrl]];
            staticAvatar = [[UIImage alloc] initWithData:data];
        }
        [cell.avatarImage sd_setImageWithURL:[NSURL URLWithString:@""] placeholderImage:staticAvatar];
    }
    else {
        UIImage* avatar = [avatarImages objectForKey:news.title];
        if (avatar == nil) {
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @60, @"height": @60}];
            NSString * avatarUrl  = [cloudinary url:news.avatarPath options:@{@"transformation": transformation}];
            
            NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:avatarUrl]];
            avatar = [[UIImage alloc] initWithData:data];
            
            [avatarImages setObject:avatar forKey:news.title];
        }
        [cell.avatarImage sd_setImageWithURL:[NSURL URLWithString:@""] placeholderImage:avatar];
    }
    
    [cell.nameLabel setText:news.title];
    NSString* date = [[DateHelper sharedSingleton] get24hTime:news.createDate];
    [cell.timeLabel setText:date];
    [cell.contentView setBackgroundColor:self.tableView.backgroundColor];
    [cell.newsText setText:news.newsDescription];
    [cell.photosView setHidden:YES];
    if ([news.photos count] > 0) {
        [cell.photosView setHidden:NO];
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsData* news = [newsArray objectAtIndex:indexPath.row];
    NewsFeedCell *cell = [tableView dequeueReusableCellWithIdentifier:NewsFeedCellIdentifier];
    cell.newsText.text = news.newsDescription;
    CGSize maximumLabelSize = CGSizeMake(cell.newsText.frame.size.width, 9999);
    CGSize expectedLabelSize = [cell.newsText sizeThatFits:maximumLabelSize];

    CGFloat height = STATIC_HEIGHT + expectedLabelSize.height;
    
    if ([news.photos count] > 0) {
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
    
    return [news.photos count];
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NewsPhotoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:PhotoCellIdentifier forIndexPath:indexPath];
    int tag = (int)collectionView.tag;
    NewsData* news = [newsArray objectAtIndex:tag];
     
    NSString *indexKey = [@(indexPath.item) stringValue];
    UIImage* img = [news.tempDownlaodedPhotos objectForKey:indexKey];
    if (img == nil) {
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        NSString * url = [news.photos objectAtIndex:indexPath.item];
        NSString * imageUrl  = [cloudinary url:url options:@{}];
        
        NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:imageUrl]];
        img = [[UIImage alloc] initWithData:data];
        
        [news.tempDownlaodedPhotos setObject:img forKey:indexKey];
    }
    [cell.photoImageView sd_setImageWithURL:[NSURL URLWithString:@""] placeholderImage:img];

    cell.photoImageView.contentMode = UIViewContentModeScaleAspectFill;
    return cell;
    
}

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    int tag = (int)collectionView.tag;
    NewsData* news = [newsArray objectAtIndex:tag];
    photoSlideShowUrls = news.photos;
    NSInteger itemClicked = indexPath.item;
    
    EBPhotoPagesController *photoPagesController = [[EBPhotoPagesController alloc]
                                                    initWithDataSource:self delegate:self photoAtIndex:itemClicked ];
    
    [self presentViewController:photoPagesController animated:YES completion:nil];
    
}

#pragma mark - Image Datasource methods
- (UIImage *)photoPagesController:(EBPhotoPagesController *)controller
                     imageAtIndex:(NSInteger)index {
    
    NSString *indexKey = [@(index) stringValue];
    UIImage* img = [photoImages objectForKey:indexKey];
    if (img == nil) {
        if (photoImages == nil) {
            photoImages = [[NSMutableDictionary alloc]init];
        }
        
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        NSString * url = [photoSlideShowUrls objectAtIndex:index];
        NSString * imageUrl  = [cloudinary url:url options:@{}];
        
        NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:imageUrl]];
        img = [[UIImage alloc] initWithData:data];
        
        NSString *inStr = [@(index) stringValue];
        [photoImages setObject:img forKey:inStr];
    }

    return img;
}

- (void)photoPagesController:(EBPhotoPagesController *)controller
                imageAtIndex:(NSInteger)index
           completionHandler:(void(^)(UIImage *image))handler {
    
}

- (BOOL)photoPagesController:(EBPhotoPagesController *)photoPagesController
    shouldExpectPhotoAtIndex:(NSInteger)index {
    if (index < [photoSlideShowUrls count]) {
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

- (void)insertRowAtTop {
    [self loadNews:0 limit:5 refreshing:YES];
}

- (void)insertRowAtBottom {
    int countToSkip = (int)[newsArray count];
    [self loadNews:countToSkip limit:3 refreshing:NO];
}


@end
