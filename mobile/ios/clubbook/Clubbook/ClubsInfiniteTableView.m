//
//  ClubsInfiniteTableView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "ClubsInfiniteTableView.h"
#import "PlaceCell.h"
#import "LocationHelper.h"
#import "UIImageView+WebCache.h"
#import "ClubProfileTabBarViewController.h"

@implementation ClubsInfiniteTableView

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken{
    self = [super initWithFrame:frame userLat:userLat userLon:userLon accessToken:accessToken];
    if (self) {
        UINib *nib = [UINib nibWithNibName:@"PlaceCell" bundle:nil];
        [self registerNib:nib forCellReuseIdentifier:@"PlaceCell"];
    }
    
    return self;
}

- (void) refreshData {
    [super refreshData];
    [self loadPlaceType:@"" take:10 skip:0];
}

- (void) loadMoreData {
    [super loadMoreData];
    int countToSkip = (int)[self.dataArray count];
    [self loadPlaceType:@"" take:10 skip:countToSkip];
}

- (void)loadPlaceType:(NSString*) type take:(int)take skip:(int)skip
{
    [self.manager retrievePlaces:self.userLat lon:self.userLon take:take skip:skip distance:0 search:@"" accessToken:self.accessToken];
}

- (void)didReceivePlaces:(NSArray *)places
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updateTableWithData:places];
    });
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    PlaceCell *cell = [self dequeueReusableCellWithIdentifier:@"PlaceCell" forIndexPath:indexPath];
    Place *place = self.dataArray[indexPath.row];
    
    [cell.clubNameText setText:place.title];
    
    cell.userCountLabelTitle.text = NSLocalizedString(@"checkedIn", nil);
    
    cell.friendsCountLabelTitle.text = NSLocalizedString(@"friends_lower", nil);
    
    int disatanceInt = (int)place.distance;
    
    [cell.distanceLabel setText:[LocationHelper convertDistance:disatanceInt]];
    
    [cell.userCountLabel setText: [NSString stringWithFormat:@"%d", place.countOfUsers]];
    
    [cell.friendsCountLabel setText: [NSString stringWithFormat:@"%d", place.friendsCount]];
    
    [cell.clubAvatar sd_setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
     NSIndexPath *selectedIndexPath = [self indexPathForSelectedRow];
     Place *place = self.dataArray[selectedIndexPath.row];
     
     UIStoryboard *clubProfileStoryboard = [UIStoryboard storyboardWithName:@"ClubProfileStoryboard" bundle: nil];
     ClubProfileTabBarViewController *clubController  = [clubProfileStoryboard instantiateInitialViewController];
     clubController.place = place;
    
    [self transitToController:clubController];
    
    [self deselectRowAtIndexPath:indexPath animated:NO];
}

@end
