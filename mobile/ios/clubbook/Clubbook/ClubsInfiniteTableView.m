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

@implementation ClubsInfiniteTableView
{
    BOOL isRefreshing;
}

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken{
    self = [super initWithFrame:frame userLat:userLat userLon:userLon accessToken:accessToken];
    if (self) {
        self.delegate = self;
        self.dataSource = self;
        
        UINib *nib = [UINib nibWithNibName:@"PlaceCell" bundle:nil];
        [self registerNib:nib forCellReuseIdentifier:@"PlaceCell"];
    }
    
    return self;
}

- (void)insertRowAtTop {
    [super insertRowAtTop];
    [self loadPlaceType:@"" take:10 skip:0 refreshing:YES];
}

- (void)insertRowAtBottom {
    [super insertRowAtBottom];
    int countToSkip = (int)[self.places count];
    [self loadPlaceType:@"" take:10 skip:countToSkip refreshing:NO];
}

- (void) refreshPlaces {
    [self loadPlaceType:@"" take:10 skip:0 refreshing:YES];
}

- (void)loadPlaceType:(NSString*) type take:(int)take skip:(int)skip refreshing:(BOOL) refreshing
{
    isRefreshing = refreshing;
    [self.manager retrievePlaces:self.userLat lon:self.userLon take:take skip:skip distance:0 search:@"" accessToken:self.accessToken];
}

- (void) makeInitialLoad {
    [super makeInitialLoad];
    [self refreshPlaces];
}

- (void)didReceivePlaces:(NSArray *)places
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if (isRefreshing) {
            self.places = [places mutableCopy];
            isRefreshing = NO;
        }
        else {
            [self.places addObjectsFromArray:places];
        }
 
        BOOL loadedEmpty = [_places count] > 0;
        
        if (loadedEmpty) {
            [self setHidden:NO];
        }
        [self tableLoadedEmpty:loadedEmpty];
       
        [self stopAnimation];
        [self reloadData];
    });
}



- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.places.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    PlaceCell *cell = [self dequeueReusableCellWithIdentifier:@"PlaceCell" forIndexPath:indexPath];
    Place *place = self.places[indexPath.row];
    
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
    /*NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
     Place *place = _places[selectedIndexPath.row];
     
     UIStoryboard *clubProfileStoryboard = [UIStoryboard storyboardWithName:@"ClubProfileStoryboard" bundle: nil];
     ClubProfileTabBarViewController *clubController  = [clubProfileStoryboard instantiateInitialViewController];
     clubController.place = place;
     
     [UIView beginAnimations:@"animation" context:nil];
     [UIView setAnimationDuration:0.5];
     [self.navigationController pushViewController: clubController animated:NO];
     [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
     [UIView commitAnimations];
     //[self.clubTable deselectRowAtIndexPath:indexPath animated:NO];*/
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 75.0f;
}

@end
