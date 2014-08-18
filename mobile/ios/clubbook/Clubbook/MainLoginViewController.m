//
//  MainLoginViewController.m
//  Clubbook
//
//  Created by Andrew on 6/17/14.
//  Copyright (c) 2014 Appcoda. All rights reserved.
//

#import "MainLoginViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "Convertor.h"
#import <Parse/Parse.h>
#import "SessionHelper.h"

@interface MainLoginViewController ()<ClubbookManagerDelegate> {
    User *_user;
}
@end

@implementation MainLoginViewController

- (IBAction)unwindToRed:(UIStoryboardSegue *)unwindSegue
{
    //[self performSegueWithIdentifier: @"onError" sender: self];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (IBAction)unwindToLoginViewController:(UIStoryboardSegue *)unwindSegue
{
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Do any additional setup after loading the view.
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    if([userId length] > 0)
    {
        [self performSegueWithIdentifier: @"onLogin" sender: self];
    }
    
    self.sloganLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:20];
    self.sloganLabel.text = NSLocalizedString(@"slogan", nil);
    self.fbButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    [self.fbButton setTitle:NSLocalizedString(@"fbSign", nil) forState:UIControlStateNormal];
    self.regButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    [self.regButton setTitle:NSLocalizedString(@"emailSignUp", nil) forState:UIControlStateNormal];
    self.loginButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    [self.loginButton setTitle:NSLocalizedString(@"emailSignIn", nil) forState:UIControlStateNormal];
    
    self.termOfUseLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    self.termOfUseLabel.text = NSLocalizedString(@"termsText", nil);
    self.termOfUseButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:12];
    [self.termOfUseButton setTitle:NSLocalizedString(@"termsAction", nil) forState:UIControlStateNormal];
  
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES];   //it hides
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    // it shows
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (IBAction)termsAction:(id)sender {
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:NSLocalizedString(@"termsUrl", nil)]];
}

- (IBAction)fbAction:(id)sender {
    
    // FBSample logic
    // Check to see whether we have already opened a session.
    
    [self showProgress:NO title:nil];

    if (FBSession.activeSession.isOpen) {
         [self doFbLogin];
        
    } else {

        dispatch_async(dispatch_get_main_queue(), ^{
            NSArray *permissions = [[NSArray alloc] initWithObjects: @"public_profile", @"email", @"user_friends",@"user_birthday", @"user_hometown", nil];
            [FBSession openActiveSessionWithReadPermissions:permissions
                                               allowLoginUI:YES
                                          completionHandler:^(FBSession *session,
                                                              FBSessionState status,
                                                              NSError *error)
            {
                
                
                if (error) {
                    [self hideProgress];                              
                } else if (FB_ISSESSIONOPENWITHSTATE(status)) {
                                              
                    [self doFbLogin];
                }
            }];
        });
    }
}

- (void)doFbLogin {
    dispatch_async(dispatch_get_main_queue(), ^{

        [[FBRequest requestForMe] startWithCompletionHandler:
         ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *user, NSError *error) {
             if (error) {
                 [self hideProgress];
                 return;
             }
             
             NSString *query = [NSString stringWithFormat:@"SELECT hometown_location FROM   user WHERE uid = %@", user.id];
             // Set up the query parameter
             NSDictionary *queryParam = @{ @"q": query };
             // Make the API request that uses FQL
             [FBRequestConnection startWithGraphPath:@"/fql"
                                          parameters:queryParam
                                          HTTPMethod:@"GET"
                                   completionHandler:^(FBRequestConnection *connection,
                                                       id result,
                                                       NSError *error) {
                                       if (error) {
                                           [self hideProgress];
                                       } else {
                                           NSArray *data = [result objectForKey:@"data"];
                                           NSString *country = @"";
                                           if (data.count > 0) {
                                               FBGraphObject *ht = [[data objectAtIndex:0] objectForKey:@"hometown_location"];
                                               if (ht != nil && ht != (id)[NSNull null]) {
                                                   country = [ht objectForKey:@"country"];
                                               }

                                           }
                                           
                                           [self onFbLogin:user country:country];
                                       }
                                   }];
             
             
         }];
    });
}


-(void) onFbLogin:(NSDictionary<FBGraphUser> *) user country:(NSString *) country {
    
    NSString *accessToken = [[FBSession activeSession] accessToken];
    NSString *expirationDate = [[FBSession activeSession] expirationDate];
    NSString *name  = [user objectForKey:@"first_name"];
    NSString *email  = [user objectForKey:@"email"];
    NSString *gender  = [user objectForKey:@"gender"];
    NSString *birthday = [user objectForKey:@"birthday"];
    NSString *userid  = user.id;
    
    NSString *dob;
    dob = [self formatDate:birthday];
    
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    
    CLUploader* uploader = [[CLUploader alloc] init:cloudinary delegate:self];
  
    //[self showProgress:NO title:nil];
    
    NSString *avatarUrl = [NSString stringWithFormat:@"https://graph.facebook.com/%@/picture?width=700&height=700", user.id];
    [uploader upload:avatarUrl options:@{@"public_id": userid} withCompletion:^(NSDictionary *successResult, NSString *errorResult, NSInteger code, id context) {
        if (successResult) {
            dispatch_async(dispatch_get_main_queue(), ^{
                NSString *JSONresult = [Convertor convertDictionaryToJsonString:successResult];
                
                [self._manager fbLoginUser:userid fbAccessToken:accessToken fbAccessExpires:expirationDate gender:gender name:name email:email avatar:JSONresult bio:NSLocalizedString(@"defoutBio", nil) country:country dob:dob];

            });
        } else {
            NSLog(@"Block upload error: %@, %ld", errorResult, (long)code);
            
        }
    } andProgress:^(NSInteger bytesWritten, NSInteger totalBytesWritten, NSInteger totalBytesExpectedToWrite, id context) {
        NSLog(@"Block upload progress: %ld/%ld (+%ld)", (long)totalBytesWritten, (long)totalBytesExpectedToWrite, (long)bytesWritten);
    }];
        
}

- (NSString *)formatDate:(NSString *)birthday {
    if (birthday == nil) {
        return nil;
    }
    
    NSDateFormatter* myFormatter = [[NSDateFormatter alloc] init];
    [myFormatter setDateFormat:@"MM/dd/yyyy"];
    NSDate* birthdayDate = [myFormatter dateFromString:birthday];
    
    NSCalendar* calendar = [NSCalendar currentCalendar];
    NSDateComponents* components = [calendar components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:birthdayDate]; // Get necessary date components
    
    NSInteger day = [components day];
    NSInteger month = [components month];
    NSInteger year = [components year];
    NSString *dob = [[NSString alloc] initWithFormat:@"%02d.%02d.%04d", day, month, year];
    return dob;
}



- (void)didFbLoginUser:(User *) user {
    _user = user;
    
    [self hideProgress];
    [SessionHelper StoreUser:_user];

    [self performSegueWithIdentifier: @"onLogin" sender: self];
    
}

@end
