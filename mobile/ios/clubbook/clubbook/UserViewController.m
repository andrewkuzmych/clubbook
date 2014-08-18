//
//  UserViewController.m
//  Clubbook
//
//  Created by Andrew on 7/2/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "UserViewController.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "ChatViewController.h"
#import "UIButton+WebCache.h"
#import "UIView+StringTagAdditions.h"
#import "CSNotificationView.h"

@interface UserViewController (){
    User *_user;
    float oldX;
    float oldUserX;
}


@end

@implementation UserViewController

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
    
    self.connectButton.layer.borderColor = [UIColor whiteColor].CGColor;
    self.nameLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:16.0];
    self.connectButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:14.0];
    self.ageLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    self.addFriendButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
    self.countryLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
    self.aboutMeTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15.0];
    self.aboutMeTitleLabel.text = NSLocalizedString(@"aboutMe", nil);
    self.aboutMeLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
    
    [self loadUser];
    // Do any additional setup after loading the view.
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.translucent = YES;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.translucent = NO;
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)loadUser
{
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        [self._manager retrieveFriend:self.userId accessToken:accessToken];
        [self showProgress:YES title:nil];
    });
}

- (void)didReceiveFriend:(User *)user
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
        
        for (int i = 0; i < [user.photos count]; i++) {
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
            NSString *avatarUrl = [cloudinary url: [[user.photos objectAtIndex:i] valueForKey:@"public_id"] options:@{@"transformation": transformation}];

            [imageView setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"Default.png"]];
            [imageView setBackgroundColor:[UIColor blackColor]];
            [self.imageScrollView addSubview:imageView];
            
        }
        
        [self.addFriendButton setButtonState:user.friend_status];

        self.nameLabel.text = user.name;
        self.imageScrollView.delegate = self;
        
        NSString *firstCapChar = [[user.country substringToIndex:1] capitalizedString];
        NSString *cappedCountry = [user.country stringByReplacingCharactersInRange:NSMakeRange(0,1) withString:firstCapChar];
        self.countryLabel.text = cappedCountry;
        
        self.checkinLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
        if (self.clubCheckinName != nil) {
            self.checkinLabel.hidden = NO;
            [self.checkinLabel setText: [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"checked_in", nil), self.clubCheckinName]];
        } else {
            self.checkinLabel.hidden = YES;
        }

        
        if (user.bio != nil) {
            self.aboutMeLabel.text = user.bio;
        } else {
            self.aboutMeLabel.text = NSLocalizedString(@"defoutBio", nil);
        }
        NSString* userAge = user.age;
        NSString* userGender = user.gender;
        
        //defoutBio
        if (userAge != nil) {
            self.ageLabel.text = [NSString stringWithFormat:@"%@, %@",userAge, NSLocalizedString(userGender, nil)];
        }
        
        self.imagePageView.numberOfPages = self.imageScrollView.subviews.count;
        
        self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [user.photos count]  , self.imageScrollView.frame.size.height + 2);
            
        _user = user;
  
        [self pupulateOtherUserPhotos];
        
        
    });
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
    User *user = [self.currentPlace.users objectAtIndex:button.tag];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    if([user.id isEqualToString:userId]) {
        // cannot see own profile :)
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"cannotSeeOwnPofile", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
        return;
    }

    self.userId = user.id;
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
    [self.addFriendButton setButtonState:NSLocalizedString(@"receiveRequest", nil)];
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
        chatController.userTo = self.userId;
    }
}

- (IBAction)chatAction:(id)sender {
    [self performSegueWithIdentifier: @"onChat" sender: sender];
}

- (IBAction)addFriendAction:(AddFriendButton *)sender {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];

    if ([sender.friendState isEqualToString:NSLocalizedString(@"noneFriend", nil)]) {
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager sendFriendReguest:userId friendId:self.userId accessToken:accessToken];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"receiveRequest", nil)]){
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager confirmFriendRequest:userId friendId:self.userId accessToken:accessToken];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"friend", nil)]){
        UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSureUnfriend", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                                NSLocalizedString(@"yes", nil),
                                nil];
        [popup showInView:[UIApplication sharedApplication].keyWindow];
    }
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
            [self._manager removeFriend:userId friendId:self.userId accessToken:accessToken];
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
