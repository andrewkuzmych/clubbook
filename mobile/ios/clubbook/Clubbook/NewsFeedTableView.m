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
    NSArray* photoSlideShowUrls;
    NSMutableDictionary* photoImages;
    NSMutableDictionary* avatarImages;
    UIImage* staticAvatar;
}

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

- (void) refreshData {
    [super refreshData];
    [self loadNews:10 skip:0];
}

- (void) loadMoreData {
    [super loadMoreData];
    int countToSkip = (int)[self.dataArray count];
    [self loadNews:10 skip:countToSkip];
}

- (void) initializeNewsTableType:(double) userLat userLon:(double)userLon accessToken:(NSString*) accessToken type:(NSString*) type objectId:(NSString*) objectId andParentViewCntroller:(UIViewController*) parent {
    self.type = type;
    self.newsObjectId = objectId;
    self.parentViewController = parent;
    
    [self initData:userLat userLon:userLon accessToken:accessToken];
    
    [self loadNews:5 skip:0];
}

- (void) initData {
    UINib *nib = [UINib nibWithNibName:@"NewsFeedCell" bundle:nil];
    [self registerNib:nib forCellReuseIdentifier:@"NewsFeedCell"];
    avatarImages = [[NSMutableDictionary alloc] init];
}

- (void) loadNews:(int)take skip:(int)skip{
  [self.manager retrieveNews:self.type withId:self.newsObjectId accessToken:self.accessToken skip:skip limit:take userLon:self.userLon userLat:self.userLat];
}

- (void) didReceiveNews:(NSArray*) news {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updateTableWithData:news];
    });
}

- (UIImage*) getProperAvatarImage:(NSString*) type newsData:(NewsData*) news {
    
    NSString* title = @"";
    NSString* avatarPath = @"";
    
    if (news.place) {
        title = news.place.title;
        avatarPath = news.place.avatar;
    }
    else if (news.dj) {
        title = news.dj.name;
        avatarPath = news.dj.avatar;
    }
    
    if ([type isEqualToString:@"club"]) {
        if (staticAvatar == nil) {
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @60, @"height": @60}];
            NSString * avatarUrl  = [cloudinary url:avatarPath options:@{@"transformation": transformation}];
            
            NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:avatarUrl]];
            staticAvatar = [[UIImage alloc] initWithData:data];
        }
        return staticAvatar;
    }
    else {
        UIImage* avatar = [avatarImages objectForKey:title];
        if (avatar == nil) {
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @60, @"height": @60}];
            NSString * avatarUrl  = [cloudinary url:avatarPath options:@{@"transformation": transformation}];
            
            NSData *data = [NSData dataWithContentsOfURL:[NSURL URLWithString:avatarUrl]];
            avatar = [[UIImage alloc] initWithData:data];
            
            [avatarImages setObject:avatar forKey:title];
        }
        return avatar;
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;    //count of section
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [self.dataArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NewsFeedCell *cell = [tableView dequeueReusableCellWithIdentifier:NewsFeedCellIdentifier];
    UINib *nib = [UINib nibWithNibName:@"NewsPhotoCell" bundle:nil];
    [cell.photosView registerNib:nib forCellWithReuseIdentifier:@"NewsPhotoCell"];
    if (cell == nil) {
        cell = [[NewsFeedCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:NewsFeedCellIdentifier];
        
    }
    NewsData* news = [self.dataArray objectAtIndex:indexPath.row];
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

    NSString* title = @"";
    
    if (news.place) {
        title = news.place.title;
    }
    else if (news.dj) {
        title = news.dj.name;
    }
    
    [cell.nameLabel setText:title];
    NSString* date = [[DateHelper sharedSingleton] get24hTime:news.createDate];
    
    NSDateFormatter* dayFormatter = [[NSDateFormatter alloc] init];
    [dayFormatter setDateFormat:@"EEEE"];
    NSString* day = [dayFormatter stringFromDate:news.createDate];
    
    NSTimeInterval daysNow = [[NSDate date] timeIntervalSinceReferenceDate];
    NSTimeInterval daysStart = [news.createDate timeIntervalSinceReferenceDate];

    NSDateComponentsFormatter *formatter = [[NSDateComponentsFormatter alloc] init];
    int timePast =  daysNow - daysStart;
    if (timePast > 86400) {
        formatter.allowedUnits = NSCalendarUnitDay;
    } else {
        formatter.allowedUnits = NSCalendarUnitHour;
    }
    
    formatter.unitsStyle = NSDateComponentsFormatterUnitsStyleAbbreviated;
    NSString* timeString = [formatter stringFromDate:news.createDate toDate:[NSDate date]];
    
    NSString* formattedString = [NSString stringWithFormat:@"%@ @ %@ (%@ ago)", day, date, timeString, nil];
    
    [cell.timeLabel setText:formattedString];

    
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
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    NewsData* news = [self.dataArray objectAtIndex:indexPath.row];
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
    NewsData* news = [self.dataArray objectAtIndex:tag];
    
    return [news.photos count];
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NewsPhotoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:PhotoCellIdentifier forIndexPath:indexPath];
    int tag = (int)collectionView.tag;
    __block NewsData* news = [self.dataArray objectAtIndex:tag];
    
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
    NewsData* news = [self.dataArray objectAtIndex:tag];
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

@end
