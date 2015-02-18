//
//  NewsFeedTableView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/27/15.
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
#import "LocationManagerSingleton.h"

#define STATIC_HEIGHT 100

#import "NewsFeedTableView.h"

@implementation NewsFeedTableView
{
    NSMutableArray* newsArray;
    NSArray* photoSlideShowUrls;
    NSMutableDictionary* photoImages;
    NSMutableDictionary* avatarImages;
    UIImage* staticAvatar;
    NSString *accessToken;
    
    BOOL isRefreshingNews;
    
    double userLon;
    double userLat;
}
static ClubbookManager* manager;
static NSString* NewsFeedCellIdentifier = @"NewsFeedCell";
static NSString* PhotoCellIdentifier = @"NewsPhotoCell";

- (id) initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self initData];
    }
    return self;
}

- (id) initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self initData];
    }
    return self;
}

-(void) initializeNewsTableType:(NSString*) type objectId:(NSString*) objectId andParentViewCntroller:(UIViewController*) parent {
    self.type = type;
    self.newsObjectId = objectId;
    self.parentViewController = parent;
    manager = [[ClubbookManager alloc] init];
    manager.communicator = [[ClubbookCommunicator alloc] init];
    manager.communicator.delegate = manager;
    manager.delegate = self;
    
    [self loadNews:0 limit:5 refreshing:YES];
}

- (void) initData {
    newsArray = [[NSMutableArray alloc] init];
    
    avatarImages = [[NSMutableDictionary alloc] init];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    accessToken = [defaults objectForKey:@"accessToken"];
    
    __weak NewsFeedTableView *weakSelf = self;
    // setup pull-to-refresh
    [self addPullToRefreshWithActionHandler:^{
        [weakSelf insertRowAtTop];
    }];
    
    // setup infinite scrolling
    [self addInfiniteScrollingWithActionHandler:^{
        [weakSelf insertRowAtBottom];
    }];
    
    self.delegate = self;
    self.dataSource = self;
}

- (void) loadNews:(int)skip limit:(int)limit refreshing:(BOOL)refreshing {
    isRefreshingNews = refreshing;
    if ([self.type isEqualToString:@"events"]) {
        userLat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
        userLon = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    }
    [manager retrieveNews:self.type withId:self.newsObjectId accessToken:accessToken skip:skip limit:limit userLon:userLon userLat:userLat];
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
            [self reloadData];
        }
    }
    else {
        [self setHidden:YES];
    }
    
    [self.pullToRefreshView stopAnimating];
    [self.infiniteScrollingView stopAnimating];
}

- (UIImage*) getProperAvatarImage:(NSString*) type newsData:(NewsData*) news {
    if ([type isEqualToString:@"club"]) {
        if (staticAvatar == nil) {
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @60, @"height": @60}];
            NSString * avatarUrl  = [cloudinary url:news.avatarPath options:@{@"transformation": transformation}];
            
            NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:avatarUrl]];
            staticAvatar = [[UIImage alloc] initWithData:data];
        }
        return staticAvatar;
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
        return avatar;
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
    dispatch_queue_t concurrentQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(concurrentQueue, ^{
        __block UIImage *image = nil;
        
        dispatch_sync(concurrentQueue, ^{
            image = [self getProperAvatarImage:self.type newsData:news];
        });
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            [cell.avatarImage setImage:image];
        });
    });

    if ([news.type isEqualToString:@"event"]) {
        NSString* title = [NSString stringWithFormat:@"EVENT: %@", news.title];
        [cell.nameLabel setText:title];
        NSString* startTime = [[DateHelper sharedSingleton] get24hTime:news.startTime];
        NSString* endTime = [[DateHelper sharedSingleton] get24hTime:news.startTime];
        
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"dd/MM"];
        NSString* month = [formatter stringFromDate:news.startTime];
        NSString* date = [NSString stringWithFormat:@"%@ Start:%@ End:%@", month, startTime, endTime];
        [cell.timeLabel setText:date];
    }
    else {
        [cell.nameLabel setText:news.title];
        NSString* date = [[DateHelper sharedSingleton] get24hTime:news.createDate];
        [cell.timeLabel setText:date];
    }
    
    [cell.contentView setBackgroundColor:self.backgroundColor];
    [cell.newsText setText:news.newsDescription];
    [cell.photosView setHidden:YES];
    if ([news.photos count] > 0) {
        [cell.photosView setHidden:NO];
    }
    
    if (news.shareLink != nil) {
        [cell.shareButton setHidden:NO];
        cell.shareLink = news.shareLink;
    }
    if (news.buyLink != nil) {
        [cell.buyButton setHidden:NO];
        cell.buyLink = news.buyLink;
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
    __block NewsData* news = [newsArray objectAtIndex:tag];
    
    dispatch_queue_t concurrentQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(concurrentQueue, ^{
        NSString *indexKey = [@(indexPath.item) stringValue];
        __block UIImage* img = [news.tempDownlaodedPhotos objectForKey:indexKey];
        
        if (img == nil) {
            dispatch_sync(concurrentQueue, ^{
                CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
                NSString * url = [news.photos objectAtIndex:indexPath.item];
                NSString * imageUrl  = [cloudinary url:url options:@{}];
                
                NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:imageUrl]];
                img = [[UIImage alloc] initWithData:data];
                if (img != nil) {
                    [news.tempDownlaodedPhotos setObject:img forKey:indexKey];
                }
            });
        }
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            [cell.photoImageView setImage:img];
            cell.photoImageView.contentMode = UIViewContentModeScaleAspectFill;
        });
    });
    return cell;
}

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    int tag = (int)collectionView.tag;
    NewsData* news = [newsArray objectAtIndex:tag];
    photoSlideShowUrls = news.photos;
    NSInteger itemClicked = indexPath.item;
    
    EBPhotoPagesController *photoPagesController = [[EBPhotoPagesController alloc]
                                                    initWithDataSource:self delegate:self photoAtIndex:itemClicked ];
    if (self.parentViewController) {
        [self.parentViewController presentViewController:photoPagesController animated:YES completion:nil];
    }
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
