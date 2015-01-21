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
#import "CLCloudinary.h"
#import "Constants.h"
#import "UIImageView+WebCache.h"

#define STATIC_HEIGHT 100

@interface NewsFeedTableViewController ()

@end

@implementation NewsFeedTableViewController
{
    NSArray* newsArray;
    NSArray* photoSlideShowUrls;
    NSMutableDictionary* photoImages;
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
    
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
     
    [self._manager retrievePlaceNews:self.place.id accessToken:accessToken];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) didReceivePlaceNews:(NSArray*) news {
    newsArray = news;
    if ([newsArray count] > 0) {
        [self.tableView reloadData];
    }
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
    
    [cell.avatarImage sd_setImageWithURL:[NSURL URLWithString:self.place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    [cell.nameLabel setText:self.place.title];
    
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
    
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    NSString * url = [news.photos objectAtIndex:indexPath.item];
    NSString * imageUrl  = [cloudinary url:url options:@{}];
    [cell.photoImageView sd_setImageWithURL:[NSURL URLWithString:imageUrl] placeholderImage:[UIImage imageNamed:@"background"]];

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


@end
