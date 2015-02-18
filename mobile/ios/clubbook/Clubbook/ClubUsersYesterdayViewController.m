//
//  ClubUsersYesterdayViewController.m
//  Clubbook
//
//  Created by Andrew on 10/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubUsersYesterdayViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import "HeaderView.h"
#import "ClubFooterView.h"
#import "LocationManagerSingleton.h"
#import "LocationHelper.h"
#import "UIImageView+WebCache.h"
#import "UserProfileViewController.h"
#import "ProfileCell.h"
#import "Constants.h"
#import "CSNotificationView.h"
#import "Cloudinary.h"
#import "InfoViewController.h"
#import "GlobalVars.h"
#import "TransitionFromClubUsersToUser.h"
#import "ProfilePagesViewController.h"

@interface ClubUsersYesterdayViewController ()<UINavigationControllerDelegate>
{
    NSArray *_users;
    float oldX;
}
@end

@implementation ClubUsersYesterdayViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    if (self.hasBack) {
        self.navigationItem.leftBarButtonItem = nil;
    }
    
    self.title = NSLocalizedString(@"clubProfile", nil);
    
    //[self showProgress:YES title:nil];
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        [self.clubFooterView.loadingIndicator startAnimating];
        self.clubFooterView.loadingIndicator.hidden = NO;
        
        [self._manager retrievePlaceUsersYesterday:self.place.id accessToken:accessToken];
    });
    
    self.profileCollection.dataSource = self;
    self.profileCollection.delegate = self;
    [self updateFooter];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // Set outself as the navigation controller's delegate so we're asked for a transitioning object
    self.navigationController.delegate = self;
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Club Users Yesterday Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    // Stop being the navigation controller's delegate
    if (self.navigationController.delegate == self) {
        self.navigationController.delegate = nil;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.

}

- (void)didReceivePlaceUsersYesterday:(UsersYesterday *)usersYesterday;
{
    dispatch_async(dispatch_get_main_queue(), ^{
        _users = usersYesterday.users;
        [self.clubFooterView.loadingIndicator stopAnimating];
        self.clubFooterView.loadingIndicator.hidden = YES;
        int friendsCount =0;
        for (User *user in usersYesterday.users) {
            if (user.isFriend) {
                friendsCount++;
            }
        }
        
        self.clubFooterView.footerInfoLabel.hidden = usersYesterday.hasAccess;
        if (!usersYesterday.hasAccess) {
            self.clubFooterView.footerInfoLabel.text = NSLocalizedString(@"noAccessToYesterday", nil);
        } else if (_users.count == 0) {
            self.clubFooterView.footerInfoLabel.text = NSLocalizedString(@"noUsersCheckedIn", nil);
        }
        
        [self updateFooter];
        
        [self updateFooterContainer];
        
        [self.profileCollection reloadData];
    });
}

- (void) updateFooter
{
    /*if (_users.count == 0) {
        [self.clubFooterView expand];
    } else {
        [self.clubFooterView collapse];
    }*/
}

- (void) updateFooterContainer
{
    /*if (_users.count == 0) {
        self.clubFooterView.footerContainer.hidden = NO;
    } else {
        self.clubFooterView.footerContainer.hidden = YES   ;
    }*/
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if(_place == nil)
    {
        return 0;
    } else{
        return _users.count;
    }
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
  
    [self performSegueWithIdentifier: @"onUsers" sender: indexPath];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSIndexPath *)indexPath
{
    if([[segue identifier] isEqualToString:@"onUser"]){
        UserProfileViewController *userController =  [segue destinationViewController];
        User *user = _users[indexPath.row];
        userController.user= user;
        userController.currentPlace = self.place;
        userController.clubCheckinName = self.place.title;
    } else if ([[segue identifier] isEqualToString:@"onUsers"]) {
        ProfilePagesViewController *profilePagesViewController =  [segue destinationViewController];
        profilePagesViewController.profiles = _users;
        profilePagesViewController.index = indexPath.row;
    }
    else if ([[segue identifier] isEqualToString:@"onClubInfo"]) {
        InfoViewController *clubController =  [segue destinationViewController];
        [clubController fillWithPlaceData:self.place];
    }
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath {
    UICollectionReusableView *reusableview = nil;
    
    if (kind == UICollectionElementKindSectionHeader) {
        // load header
        self.headerView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"header" forIndexPath:indexPath];
        reusableview = self.headerView;
        
    }
    
    if (kind == UICollectionElementKindSectionFooter) {
        self.clubFooterView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:@"footer" forIndexPath:indexPath];
        
        reusableview = self.clubFooterView;
        [self updateFooter];
        
    }
    
    return reusableview;
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *identifier = @"Cell";
    ProfileCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        User *user = _users[indexPath.row];
        cell.friendIcon.hidden = !user.isFriend;
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @120, @"height": @120}];
        NSString * avatarUrl  = [cloudinary url: [user.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        [cell.profileAvatar sd_setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty.png"]];
    });
    return cell;
}

#pragma mark UINavigationControllerDelegate methods

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController
                                  animationControllerForOperation:(UINavigationControllerOperation)operation
                                               fromViewController:(UIViewController *)fromVC
                                                 toViewController:(UIViewController *)toVC {
    // Check if we're transitioning from this view controller to a DSLSecondViewController
    if (fromVC == self && [toVC isKindOfClass:[UserProfileViewController class]]) {
        return [[TransitionFromClubUsersToUser alloc] init];
    }
    else {
        return nil;
    }
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma mark

- (ProfileCell*)collectionViewCellForThing:(User*)user {
    NSUInteger userIndex = [_users indexOfObject:user];
    if (userIndex == NSNotFound) {
        return nil;
    }
    //static NSString *identifier = @"Cell";
    //ProfileCell *cell = [self.profileCollection dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:userIndex];
    
    return (ProfileCell*)[self.profileCollection cellForItemAtIndexPath:[NSIndexPath indexPathForRow:userIndex inSection:0]];
}


@end
