//
//  UserProfileViewController.m
//  Clubbook
//
//  Created by Andrew on 10/13/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "UserProfileViewController.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "ChatViewController.h"
#import "UIButton+WebCache.h"
#import "UIView+StringTagAdditions.h"
#import "CSNotificationView.h"
#import "LocationHelper.h"
#import "TransitionFromUserToClubUsers.h"
#import "ClubUsersViewController.h"
#import "ClubUsersYesterdayViewController.h"
#import "UserCheckinsViewController.h"
#import "ProfileImagesViewController.h"

@interface UserProfileViewController ()<UINavigationControllerDelegate>{
    // User *_user;
    float oldX;
    float oldUserX;
}
@property (nonatomic, strong) UIPercentDrivenInteractiveTransition *interactivePopTransition;

@end

@implementation UserProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = NSLocalizedString(@"userProfile", nil);
    
    [self populatedWithData];
    self.tableView.alwaysBounceVertical = NO;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // Stop being the navigation controller's delegate
    if (self.navigationController.delegate == self) {
        self.navigationController.delegate = nil;
    }
    self.navigationController.navigationBar.translucent = YES;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // Set outself as the navigation controller's delegate so we're asked for a transitioning object
    self.navigationController.delegate = self;
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"User Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
    self.navigationController.navigationBar.translucent = NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)populatedWithData
{
    UIScreenEdgePanGestureRecognizer *popRecognizer = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePopRecognizer:)];
    popRecognizer.edges = UIRectEdgeLeft;
    [self.view addGestureRecognizer:popRecognizer];
    
    
    self.connectButton.layer.borderColor = [UIColor whiteColor].CGColor;
    self.nameLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:16.0];
    self.connectButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:14.0];
    self.ageLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    self.addFriendButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
    self.blockUnblockButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
    self.countryLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
    self.aboutMeTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15.0];
    self.aboutMeTitleLabel.text = NSLocalizedString(@"aboutMe", nil);
    self.aboutMeLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    
    [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"profile" ascending:NO];
    
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    
    NSArray *sortedPhotos = [self.user.photos  sortedArrayUsingDescriptors:sortDescriptors];
    
    for (int i = 0; i < [sortedPhotos count]; i++) {
        CGRect frame;
        frame.origin.x = self.imageScrollView.frame.size.width * i;
        frame.origin.y = 0;
        frame.size = self.imageScrollView.frame.size;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @450, @"height": @450, @"crop": @"fit"}];
        //c_fit,h_450,w_450/v140483
        NSString *avatarUrl = [cloudinary url: [[sortedPhotos objectAtIndex:i] valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        
        [imageView setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty.png"]];
        [imageView setBackgroundColor:[UIColor whiteColor]];
        [self.imageScrollView addSubview:imageView];
        
    }
    
    self.imagePageControl.numberOfPages = self.imageScrollView.subviews.count;
    
    [self.addFriendButton setButtonState:self.user.friend_status];
    [self.blockUnblockButton setButtonState:NSLocalizedString(@"friend", nil)];
    
    self.nameLabel.text = self.user.name;
    self.imageScrollView.delegate = self;
    
    NSString *age = @"";
    if (self.user.age != nil) {
        age = [NSString stringWithFormat:@"%@",self.user.age];
    }
    NSString *gender = @"";
    if (self.user.gender != nil) {
        NSString *gender1 = [NSString stringWithFormat:@"%@%@",[[self.user.gender substringToIndex:1] uppercaseString],[self.user.gender substringFromIndex:1] ];
        if ([age length] > 0) {
          gender = [NSString stringWithFormat:@", %@",gender1];
        } else {
           gender = [NSString stringWithFormat:@"%@",gender1];
        }
    }
    NSString *country = @"";
    if (self.user.country != nil) {
        NSString *country1 = [NSString stringWithFormat:@"%@%@",[[self.user.country substringToIndex:1] uppercaseString],[self.user.country substringFromIndex:1] ];
        if ([age length] > 0 || [gender length] > 0) {
            country = [NSString stringWithFormat:@", %@", country1];
        } else {
            country = [NSString stringWithFormat:@"%@", country1];
        }
    }
    
    self.ageCountryLabel.text = [NSString stringWithFormat:@"%@%@%@", age, gender, country];
    
    NSString *firstCapChar = [[self.user.country substringToIndex:1] capitalizedString];
    NSString *cappedCountry = [self.user.country stringByReplacingCharactersInRange:NSMakeRange(0,1) withString:firstCapChar];
    self.countryLabel.text = cappedCountry;
    
    self.checkinLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    if (self.clubCheckinName != nil) {
        self.checkinLabel.hidden = NO;
        [self.checkinLabel setText: [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"checked_in", nil), self.clubCheckinName]];
    } else {
        self.checkinLabel.hidden = YES;
    }

    if (self.user.place != nil) {
        self.currentPlace = self.user.place;
    }
    
    if (self.currentPlace != nil) {
        self.placeCheckinButton.hidden = NO;
        [self.placeCheckinButton setTitle:[NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"checked_in", nil), self.currentPlace.title] forState:UIControlStateNormal];
    } else {

        self.placeCheckinButton.hidden = YES;
    }
    
    self.friendImage.hidden = YES;
    if (self.user.isFriend) {
        self.friendImage.hidden = NO;
    }
    
    if (self.user.bio != nil) {
        self.aboutMeLabel.text = self.user.bio;
    } else {
        self.aboutMeLabel.text = NSLocalizedString(@"defoutBio", nil);
    }
    NSString* userAge = self.user.age;
    NSString* userGender = self.user.gender;
    
    //defoutBio
    if (userAge != nil) {
        self.ageLabel.text = [NSString stringWithFormat:@"%@, %@",userAge, NSLocalizedString(userGender, nil)];
    }
    
    self.imagePageView.numberOfPages = self.imageScrollView.subviews.count;
    
    self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [self.user.photos count]  , self.imageScrollView.frame.size.height + 2);
    
    if (self.user.isBlocked) {
        [self.blockUnblockButton setTitle:NSLocalizedString(@"unblockUser", nil) forState:UIControlStateNormal];
    } else {
        [self.blockUnblockButton setTitle:NSLocalizedString(@"blockUser", nil) forState:UIControlStateNormal];
    }
    
    
    [self pupulateOtherUserPhotos];
}

#pragma mark UINavigationControllerDelegate methods

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController
                                  animationControllerForOperation:(UINavigationControllerOperation)operation
                                               fromViewController:(UIViewController *)fromVC
                                                 toViewController:(UIViewController *)toVC {
    // Check if we're transitioning from this view controller to a DSLFirstViewController
    if (fromVC == self && [toVC isKindOfClass:[ClubUsersViewController class]]) {
        return [[TransitionFromUserToClubUsers alloc] init];
    } else if (fromVC == self && [toVC isKindOfClass:[ClubUsersYesterdayViewController class]]) {
        return [[TransitionFromUserToClubUsers alloc] init];
    } else if (fromVC == self && [toVC isKindOfClass:[UserCheckinsViewController class]]) {
        return [[TransitionFromUserToClubUsers alloc] init];
    }
    else {
        return nil;
    }
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController
                         interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController {
    // Check if this is for our custom transition
    if ([animationController isKindOfClass:[TransitionFromUserToClubUsers class]]) {
        return self.interactivePopTransition;
    }
    else {
        return nil;
    }
}


#pragma mark UIGestureRecognizer handlers

- (void)handlePopRecognizer:(UIScreenEdgePanGestureRecognizer*)recognizer {
    CGFloat progress = [recognizer translationInView:self.view].x / (self.view.bounds.size.width * 1.0);
    progress = MIN(1.0, MAX(0.0, progress));
    
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        // Create a interactive transition and pop the view controller
        self.interactivePopTransition = [[UIPercentDrivenInteractiveTransition alloc] init];
        [self.navigationController popViewControllerAnimated:YES];
    }
    else if (recognizer.state == UIGestureRecognizerStateChanged) {
        // Update the interactive transition's progress
        [self.interactivePopTransition updateInteractiveTransition:progress];
    }
    else if (recognizer.state == UIGestureRecognizerStateEnded || recognizer.state == UIGestureRecognizerStateCancelled) {
        // Finish or cancel the interactive transition
        if (progress > 0.5) {
            [self.interactivePopTransition finishInteractiveTransition];
        }
        else {
            [self.interactivePopTransition cancelInteractiveTransition];
        }
        
        self.interactivePopTransition = nil;
    }
    
}


- (void)loadUser
{
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        [self._manager retrieveFriend:self.user.id accessToken:accessToken];
        [self showProgress:YES title:nil];
    });
}

//- (void)didReceiveFriend:(User *)user
//{
//    dispatch_async(dispatch_get_main_queue(), ^{
//        [self hideProgress];
//
//        [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
//
//        for (int i = 0; i < [user.photos count]; i++) {
//            CGRect frame;
//            frame.origin.x = self.imageScrollView.frame.size.width * i;
//            frame.origin.y = 0;
//            frame.size = self.imageScrollView.frame.size;
//
//            UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
//            imageView.contentMode = UIViewContentModeScaleAspectFit;
//
//            // transform avatar
//            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
//            CLTransformation *transformation = [CLTransformation transformation];
//            [transformation setParams: @{@"width": @450, @"height": @450, @"crop": @"fit"}];
//            //c_fit,h_450,w_450/v140483
//            NSString *avatarUrl = [cloudinary url: [[user.photos objectAtIndex:i] valueForKey:@"public_id"] options:@{@"transformation": transformation}];
//
//            [imageView setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty.png"]];
//            [imageView setBackgroundColor:[UIColor blackColor]];
//            [self.imageScrollView addSubview:imageView];
//
//        }
//
//        [self.addFriendButton setButtonState:user.friend_status];
//
//        self.nameLabel.text = user.name;
//        self.imageScrollView.delegate = self;
//
//        NSString *firstCapChar = [[user.country substringToIndex:1] capitalizedString];
//        NSString *cappedCountry = [user.country stringByReplacingCharactersInRange:NSMakeRange(0,1) withString:firstCapChar];
//        self.countryLabel.text = cappedCountry;
//
//        self.checkinLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
//        if (self.clubCheckinName != nil) {
//            self.checkinLabel.hidden = NO;
//            [self.checkinLabel setText: [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"checked_in", nil), self.clubCheckinName]];
//        } else {
//            self.checkinLabel.hidden = YES;
//        }
//
//
//        if (user.bio != nil) {
//            self.aboutMeLabel.text = user.bio;
//        } else {
//            self.aboutMeLabel.text = NSLocalizedString(@"defoutBio", nil);
//        }
//        NSString* userAge = user.age;
//        NSString* userGender = user.gender;
//
//        //defoutBio
//        if (userAge != nil) {
//            self.ageLabel.text = [NSString stringWithFormat:@"%@, %@",userAge, NSLocalizedString(userGender, nil)];
//        }
//
//        self.imagePageView.numberOfPages = self.imageScrollView.subviews.count;
//
//        self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [user.photos count]  , self.imageScrollView.frame.size.height + 2);
//
//        _user = user;
//
//        [self setBlockButtonStatus];
//
//        [self pupulateOtherUserPhotos];
//
//
//    });
//}
//
- (void)setBlockButtonStatus
{
    if (_user.isBlocked) {
        [self.blockUnblockButton setTitle:NSLocalizedString(@"unblockUser", nil) forState:UIControlStateNormal];
    } else {
        [self.blockUnblockButton setTitle:NSLocalizedString(@"blockUser", nil) forState:UIControlStateNormal];
    }
}

- (void)pupulateOtherUserPhotos
{
    if (self.currentPlace == nil) {
        self.userPhotosScroll.hidden = YES;
        return;
    }
    
    self.userPhotosScroll.hidden = NO;
    [[self.userPhotosScroll subviews] makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    
    for (int i = 0; i < [self.currentPlace.users count]; i++) {
        User *currentUser = [self.currentPlace.users  objectAtIndex:i];
        CGFloat originX = self.userPhotosScroll.frame.size.height*i;
        
        UIButton *imageButton = [self setupImageButton:originX];
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @600}];
        
        NSString *avatarUrl = [cloudinary url: [currentUser.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        
        [imageButton setImageWithURL:[NSURL URLWithString:avatarUrl] forState:UIControlStateNormal];
        NSString* photoId = [[self.currentPlace.users objectAtIndex:i] valueForKey:@"_id"];
        imageButton.tag = i;
        
        
        [self.userPhotosScroll addSubview:imageButton];
        
        if (currentUser.isFriend) {
            UIImageView* friendIconView = [[UIImageView alloc] initWithFrame:CGRectMake(37 + self.userPhotosScroll.frame.size.height*i, 35, 25, 25)];
            friendIconView.image = [UIImage imageNamed:@"icon_avatar_friend.png"];
            [self.userPhotosScroll addSubview:friendIconView];
        }
    }
    
    self.userPhotosScroll.delegate = self;
    
    CGSize size = CGSizeMake(self.userPhotosScroll.frame.size.height * ([self.currentPlace.users count]) + 5  , self.userPhotosScroll.frame.size.height + 5);
    self.userPhotosScroll.contentSize = size;
}

- (void)changeSelectedImage:(UIButton*)button
{
    BOOL isCheckinHere = [LocationHelper isCheckinHere:self.currentPlace];
    User *user = [self.currentPlace.users objectAtIndex:button.tag];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    if (!isCheckinHere && !user.isFriend) {
        // cannot see profile when you are not checked in and not friend
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"needToCheckinFirst", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    else if([user.id isEqualToString:userId]) {
        // cannot see own profile :)
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"cannotSeeOwnPofile", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    
    self.user = user;
    [self loadUser];
}

- (UIButton *)setupImageButton:(CGFloat)originX
{
    CGRect frame;
    frame.origin.x = originX;
    frame.origin.y = 0;
    frame.size.height = self.userPhotosScroll.frame.size.height;
    frame.size.width = self.userPhotosScroll.frame.size.height;
    
    UIButton *imageButton = [UIButton buttonWithType:UIButtonTypeCustom];
    imageButton.frame = frame;
    
    [imageButton addTarget:self action:@selector(changeSelectedImage:)
          forControlEvents:UIControlEventTouchUpInside];
    imageButton.imageView.contentMode = UIViewContentModeScaleAspectFill;
    return imageButton;
}

- (void)didSendFriend:(User *)user
{
    [self hideProgress];
    [self.addFriendButton setButtonState:NSLocalizedString(@"sentRequest", nil)];
}

- (void)didConfirmFriend:(User *)user
{
    [self hideProgress];
    [self.addFriendButton setButtonState:NSLocalizedString(@"friend", nil)];
}

- (void)didRemoveFriend:(User *)user
{
    [self hideProgress];
    [self.addFriendButton setButtonState:NSLocalizedString(@"noneFriend", nil)];
}


#pragma mark - UIScrollView Delegate
- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    if (sender == self.imageScrollView) {
        [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
        
        // Update the page when more than 50% of the previous/next page is visible
        CGFloat pageWidth = self.imageScrollView.frame.size.width;
        int page = floor((self.imageScrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
        self.imagePageView.currentPage = page;
    } else if (sender == self.userPhotosScroll){
        [self.userPhotosScroll setContentOffset: CGPointMake(self.userPhotosScroll.contentOffset.x, oldUserX  )];
    }
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSIndexPath *)indexPath
{
    if([[segue identifier] isEqualToString:@"onChat"]){
        ChatViewController *chatController =  [segue destinationViewController];
        chatController.isFromUser = YES;
        chatController.userTo = self.user.id;
    }
}

- (IBAction)chatAction:(id)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    if([self.user.id isEqualToString:userId]) {
        // cannot see own profile :)
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"cannotSeeOwnPofile", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    
    if (self.isFromChat) {
        [self.navigationController popViewControllerAnimated:YES];
    } else {
        
        UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];
        ChatViewController *chatViewController  = [mainStoryboard instantiateViewControllerWithIdentifier:@"chat"];
        chatViewController.isFromUser = YES;
        chatViewController.userTo = self.user.id;
        [self.navigationController pushViewController: chatViewController animated:YES];
        
        
       // [self performSegueWithIdentifier: @"onChat" sender: sender];
    }
}

- (IBAction)addFriendAction:(AddFriendButton *)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    if([self.user.id isEqualToString:userId]) {
        // cannot see own profile :)
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"cannotSeeOwnPofile", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    
    
    if ([sender.friendState isEqualToString:NSLocalizedString(@"noneFriend", nil)]) {
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager sendFriendReguest:userId friendId:self.user.id accessToken:accessToken];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"receiveRequest", nil)]){
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager confirmFriendRequest:userId friendId:self.user.id accessToken:accessToken];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"friend", nil)]){
        UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSureUnfriend", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                                NSLocalizedString(@"yes", nil),
                                nil];
        [popup showInView:[UIApplication sharedApplication].keyWindow];
    }
}

- (IBAction)blockUnblockAction:(id)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    if([self.user.id isEqualToString:userId]) {
        // cannot see own profile :)
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"cannotSeeOwnPofile", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }
    
    if (_user.isBlocked) {
        [self showProgress:NO title:NSLocalizedString(@"unblockingUser", nil)];
        
        [self._manager unblockUser:userId friendId:_user.id accessToken:accessToken];
    } else {
        [self showProgress:NO title:NSLocalizedString(@"blockingUser", nil)];
        [self._manager blockUser:userId friendId:_user.id accessToken:accessToken];
    }
}

- (IBAction)imageAction:(id)sender {
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];
    ProfileImagesViewController *profileImagesViewController  = [mainStoryboard instantiateViewControllerWithIdentifier:@"image"];
    profileImagesViewController.user = self.user;//place.id;
    //profileImagesViewController.hasBack = YES;
    //self.isLoaded = NO;
    // ClubUsersViewController *clubController =  [segue ClubUsersViewController];
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: profileImagesViewController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
}

- (IBAction)placeCheckinAction:(id)sender {
    
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];
    ClubUsersViewController *clubController  = [mainStoryboard instantiateViewControllerWithIdentifier:@"club"];
    clubController.place = self.currentPlace;
    clubController.hasBack = YES;
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: clubController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
}

- (void)didBlockUser:(NSString *)result
{
    _user.isBlocked = YES;
    [self setBlockButtonStatus];
    [self hideProgress];
}

- (void)didUnblockUser:(NSString *)result
{
    _user.isBlocked = NO;
    [self setBlockButtonStatus];
    [self hideProgress];
}


-(void)actionSheet:(UIActionSheet *)popup clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    switch (buttonIndex) {
        case 0:
        {
            // click yes
            [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
            [self._manager removeFriend:userId friendId:self.user.id accessToken:accessToken];
            break;
        }
        case 1:
            // click cancel
            break;
        default:
            break;
    }
    
}



@end
