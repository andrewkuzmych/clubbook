//
//  DjTableView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "DjTableView.h"
#import "DjCell.h"
#import "DJ.h"
#import "UIImageView+WebCache.h"

@implementation DjTableView

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken{
    self = [super initWithFrame:frame userLat:userLat userLon:userLon accessToken:accessToken];
    if (self) {
        UINib *nib = [UINib nibWithNibName:@"DjCell" bundle:nil];
        [self registerNib:nib forCellReuseIdentifier:@"DjCell"];
    }
    
    return self;
}

- (void) refreshData {
    [super refreshData];
    [self loadPlaceTypeTake:10 skip:0];
}

- (void) loadMoreData {
    [super loadMoreData];
    int countToSkip = (int)[self.dataArray count];
    [self loadPlaceTypeTake:10 skip:countToSkip];
}

- (void) searchForWord:(NSString*) searchWord {
    [super searchForWord:searchWord];
    [self.manager retrieveDjsAndBands:self.userLat lon:self.userLon take:10 skip:0 distance:0 search:searchWord accessToken:self.accessToken];
}

- (void)loadPlaceTypeTake:(int)take skip:(int)skip
{
    [self.manager retrieveDjsAndBands:self.userLat lon:self.userLon take:take skip:skip distance:0 search:@"" accessToken:self.accessToken];
}

- (void)didReceiveDJsAndBands:(NSArray *)djs
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updateTableWithData:djs];
    });
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    DjCell *cell = [self dequeueReusableCellWithIdentifier:@"DjCell" forIndexPath:indexPath];
    DJ *dj = self.dataArray[indexPath.row];
    
    [cell.nameLabel setText:dj.name];
    [cell.musicStyleLabel setText:dj.music];
    cell.webSiteString = dj.website;
    
    [cell.avatarView sd_setImageWithURL:[NSURL URLWithString:dj.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self indexPathForSelectedRow];
    //Place *place = self.dataArray[selectedIndexPath.row];
    /*
    UIStoryboard *clubProfileStoryboard = [UIStoryboard storyboardWithName:@"ClubProfileStoryboard" bundle: nil];
    ClubProfileTabBarViewController *clubController  = [clubProfileStoryboard instantiateInitialViewController];
    clubController.place = place;
    
    [self transitToController:clubController];*/
    
    [self deselectRowAtIndexPath:indexPath animated:NO];
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
