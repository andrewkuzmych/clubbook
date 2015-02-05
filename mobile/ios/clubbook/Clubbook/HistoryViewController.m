//
//  YesterdayPlacesViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/28/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "HistoryViewController.h"
#import "CbButton.h"
#import "ClubCell.h"
#import "UIImageView+WebCache.h"
#import "ClubCheckinsViewController.h"
#import "ClubUsersYesterdayViewController.h"

@interface HistoryViewController ()

@end

@implementation HistoryViewController {
    Place* placeToView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.placesTable.dataSource = self;
    self.placesTable.delegate = self;
    
    //hide searchbar and filter tab from view
    placeToView = nil;
    
    self.title = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Yesterday Check-ins", nil)];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    [self._manager retrieveYesterdayPlacesAccessToken:accessToken];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didReceivePlaces:(NSArray *)places andTypes:(NSArray *)types
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        [self.activityIndicator setHidden:YES];
        
        _places = places;
        [self.placesTable reloadData];
        
        if ([_places count] > 0) {
            [self.noResultsLabel setHidden:YES];
        }
        else {
            [self.noResultsLabel setHidden:NO];
        }
        
        
    });
}

- (IBAction)handleYesterdayButton:(id)sender {
    CbButton* yesterDayButton = (CbButton *) sender;
    placeToView = _places[yesterDayButton.tag];
    
    [self performSegueWithIdentifier: @"onYesterday" sender: self];
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _places.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ClubCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    Place *place = _places[indexPath.row];
    
    cell.clubNameText.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    [cell.clubNameText setText:place.title];
    
    cell.userCountLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    
    cell.friendsCountLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    
    cell.userCountLabelTitle.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:10];
    
    cell.userCountLabelTitle.text = NSLocalizedString(@"checkedIn", nil);
    
    cell.friendsCountLabelTitle.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:10];
    
    cell.friendsCountLabelTitle.text = NSLocalizedString(@"friends_lower", nil);
    
    cell.closingLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:10];
    
    cell.closingValueLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:10];
    
    [cell.userCountLabel setText: [NSString stringWithFormat:@"%d", place.countOfUsers]];
    
    [cell.friendsCountLabel setText: [NSString stringWithFormat:@"%d", place.friendsCount]];
    
    [cell.clubAvatar sd_setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    cell.viewYesterdayButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15];
    [cell.viewYesterdayButton setMainState:NSLocalizedString(@"View", nil)];

    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.placesTable indexPathForSelectedRow];
    Place *place = _places[selectedIndexPath.row];
    
    UIStoryboard *clubProfileStoryboard = [UIStoryboard storyboardWithName:@"ClubProfileStoryboard" bundle: nil];
    ClubCheckinsViewController *clubController  = [clubProfileStoryboard instantiateViewControllerWithIdentifier:@"club"];
    clubController.place = place;
    clubController.hasBack = YES;

    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: clubController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
    [self.placesTable deselectRowAtIndexPath:indexPath animated:NO];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSString *)sender
{
    if ([[segue identifier] isEqualToString:@"onYesterday"]) {
        ClubUsersYesterdayViewController *yesterdayController =  [segue destinationViewController];
        if (placeToView != nil) {
            yesterdayController.place = placeToView;
            yesterdayController.hasBack = YES;
            placeToView = nil;
        }
    }
}


@end
