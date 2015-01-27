//
//  ClubSubscribeSettingsTableViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/5/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "ClubSubscribeSettingsTableViewController.h"

@interface ClubSubscribeSettingsTableViewController ()

@end

@implementation ClubSubscribeSettingsTableViewController
{
    NSString* accessToken;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if (self.place) {
        [self.clubTitleLabel setText:self.place.title];
    }
    self.tableView.allowsSelection = NO;
    self.tableView.scrollEnabled = NO;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    accessToken = [defaults objectForKey:@"accessToken"];
    
    [self.favoriteButton setOn:self.place.isFavorite];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 70;
}
- (IBAction)onFavoriteButtonValueChanged:(id)sender {
    BOOL makeFavorite = [self.favoriteButton isOn];
    self.place.isFavorite = makeFavorite;
    [self._manager makePlaceFavorite:self.place.id accessToken:accessToken makeFavorite:makeFavorite];
}

@end
